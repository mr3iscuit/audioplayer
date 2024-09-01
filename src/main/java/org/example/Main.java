package org.example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException, URISyntaxException {
        AudioPostDTO audio = AudioPostDTO.builder()
                .album("some album")
                .artist("some artist")
                .title("some title")
                .genre("some genre")
                .releaseYear("2017")
                .trackNumber("1")
                .build();

        AudioUploader.postAudio(audio);
    }
}