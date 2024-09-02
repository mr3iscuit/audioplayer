package org.example;

import org.example.model.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException, URISyntaxException {

        AuthenticationRequest auth = new AuthenticationRequest().builder()
                .email("eyvaz.bayramov2018@gmail.com")
                .password("biscobisco")
                .build();
        AudioService audioService = new AudioService(auth);

//        RegisterRequest registerRequest = new RegisterRequest().builder()
//                .email("eyvaz.bayramov2018@gmail.com")
//                .password("biscobisco")
//                .role(Role.USER)
//                .firstname("Eyvaz")
//                .lastname("Bayramov")
//                .build();
//        AudioService audioService = new AudioService(registerRequest);


        AudioRequest audio = AudioRequest.builder()
                .album("some album")
                .artist("some artist")
                .title("some title")
                .genre("some genre")
                .releaseYear("2017")
                .trackNumber("1")
                .build();

        AudioResoponse audioResponse = audioService.postAudio(audio);
        File file = new File("/home/biscuit/Documents/vzlom.txt");
        byte[] chunk = Files.readAllBytes(file.toPath());

        audioService.uploadChunk(0, audioResponse.getId(), chunk);
    }
}