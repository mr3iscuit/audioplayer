package org.example;

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

public class AudioService {

    private Token token;
    private ObjectMapper objectMapper;
    private HttpClient client;

    private void initializeFields() {
        this.objectMapper = new ObjectMapper();
        this.client = HttpClient.newHttpClient();

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
        this.token = objectMapper.readValue(jsonString, Token.class);
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

        this.token = objectMapper.readValue(jsonString, Token.class);
    }

    public AudioGetRequest postAudio(AudioPostRequest dto) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(dto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/audio"))
                .header("Accept", "*/*")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.getAccessToken())
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

        return objectMapper.readValue(jsonString, AudioGetRequest.class);
    }

}