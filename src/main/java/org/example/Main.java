package org.example;

import org.example.model.AudioPostRequest;
import org.example.model.AuthenticationRequest;
import org.example.model.RegisterRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException, URISyntaxException {

        AuthenticationRequest auth = new AuthenticationRequest().builder()
                .email("eyvaz.bayramov2018@gmail.com")
                .password("biscobisco")
                .build();
        AudioService audioService = new AudioService(auth);

//        RegisterRequest registerRequest = new RegisterRequest().builder()
//                .email("eyvaz2.bayramov2018@gmail.com")
//                .password("biscobisco")
//                .role(Role.USER)
//                .firstname("Eyvaz")
//                .lastname("Bayramov")
//                .build();
//        AudioService audioService = new AudioService(registerRequest);


        AudioPostRequest audio = AudioPostRequest.builder()
                .album("some album")
                .artist("some artist")
                .title("some title")
                .genre("some genre")
                .releaseYear("2017")
                .trackNumber("1")
                .build();

        audioService.postAudio(audio);
    }
}