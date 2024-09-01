package org.example;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import javax.net.ssl.SSLParameters;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.SSLContext;
import java.io.IOException;
public class AudioUploader {
    public static void postAudio(AudioPostDTO dto) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(dto);

        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHJpbmciLCJpYXQiOjE3MjUyMjU2MTcsImV4cCI6MTcyNTMxMjAxN30.4seC8rN9kXrQe1apgAgZEBvBoYiel_2SsYSwWcnp-8Q";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/audio"))
                .header("accept", "*/*")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }
}