package org.example;

import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.model.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AudioService {

    private AtomicReference<Token> token;

    private ObjectMapper objectMapper;
    private HttpClient client;

    private void initializeFields() {
        this.objectMapper = new ObjectMapper();
        this.client = HttpClient.newHttpClient();
        token = new AtomicReference<>();

        objectMapper.registerModule(new JavaTimeModule());
    }

    AudioService(RegisterRequest registerRequest) {
        initializeFields();
        register(registerRequest);
    }

    AudioService(AuthenticationRequest authenticationRequest) {
        initializeFields();
        authenticate(authenticationRequest);
    }

    @SneakyThrows
    public void authenticate(AuthenticationRequest authenticationRequest) {

        String requestBody = objectMapper.writeValueAsString(authenticationRequest);

        HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/auth/authenticate"))
                    .header("accept", "*/*")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to post audio: HTTP code " + response.statusCode());
        }

        String jsonString = response.body();
        this.token.set(objectMapper.readValue(jsonString, Token.class));
    }

    @SneakyThrows
    public void register(RegisterRequest registerRequest) {
        String requestBody = objectMapper.writeValueAsString(registerRequest);

        HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/auth/register"))
                    .header("accept", "*/*")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to post audio: HTTP code " + response.statusCode());
        }

        String jsonString = response.body();
        this.token.set(objectMapper.readValue(jsonString, Token.class));
    }

    public AudioResponse postAudio(AudioRequest dto) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(dto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/audio"))
                .header("Accept", "*/*")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get().getAccessToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 403) {
            throw new RuntimeException("Access denied: HTTP code 403. Check token permissions.");
        } else if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to post audio: HTTP code " + response.statusCode());
        }

        String jsonString = response.body();
        if (jsonString == null || jsonString.isEmpty()) {
            throw new IOException("Empty response body");
        }

        return objectMapper.readValue(jsonString, AudioResponse.class);
    }

    @SneakyThrows
    public void uploadChunk(Long audioId, int chunkIndex, byte[] chunk) {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        String boundary = "----Boundary" + System.currentTimeMillis();
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofByteArrays(
                List.of(
                        ("--" + boundary + "\r\n" +
                                "Content-Disposition: form-data; name=\"file\"; filename=\"chunkIndex" + chunkIndex + "audioId" + audioId + "\"\r\n" +
                                "Content-Type: application/octet-stream\r\n\r\n").getBytes(),
                        chunk,
                        ("\r\n--" + boundary + "--\r\n").getBytes()
                )
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/audio/" + audioId + "/upload-chunk?chunkIndex=" + chunkIndex))
                .header("Accept", "*/*")
                .header("Authorization", "Bearer " + token.get().getAccessToken())
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(bodyPublisher)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 403) {
            throw new RuntimeException("Access denied: HTTP code 403. Check token permissions." + response.body());
        } else if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to upload file: HTTP code " + response.statusCode());
        }
    }

    @SneakyThrows
    public void downloadChunk(Long audioId, int chunkIndex, byte[] buffer) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/audio/" + audioId + "/download-chunk?chunkIndex=" + chunkIndex))
                .header("accept", "*/*")
                .header("Authorization", "Bearer " + token.get().getAccessToken())
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to download chunk" + response.statusCode());
        }

        System.arraycopy(response.body(), 0, buffer, 0, response.body().length);
    }

    public void uploadAudioFile(AudioResponse audioResponse, String sourceFilePath) throws IOException {

        try (FileInputStream fis = new FileInputStream(sourceFilePath)) {
            byte[] buffer = new byte[524288];
            int bytesRead;
            int chunkIndex = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                uploadChunk(audioResponse.getId(), chunkIndex, buffer);
                chunkIndex++;
            }
        }
    }

    public AudioResponse getAudioByAudioId(Long audioId) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/audio/" + audioId))
                .header("Accept", "*/*")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get().getAccessToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 403) {
            throw new RuntimeException("Access denied: HTTP code 403. Check token permissions.");
        } else if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to GET audio: HTTP code " + response.statusCode());
        }

        String jsonString = response.body();
        if (jsonString == null || jsonString.isEmpty()) {
            throw new IOException("Empty response body");
        }

        return objectMapper.readValue(jsonString, AudioResponse.class);
    }
}