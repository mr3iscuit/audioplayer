package org.example;

import org.example.model.*;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {

        AudioService audioService;

        boolean login = true;

        if(login) {
            AuthenticationRequest auth = AuthenticationRequest.builder()
                    .email("eyvaz.bayramov2018@gmail.com")
                    .password("biscobisco")
                    .build();
            audioService = new AudioService(auth);
        } else {
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .email("eyvaz.bayramov2018@gmail.com")
                    .password("biscobisco")
                    .role(Role.USER)
                    .firstname("Eyvaz")
                    .lastname("Bayramov")
                    .build();
            audioService = new AudioService(registerRequest);
        }

        AudioRequest audio = AudioRequest.builder()
                .album("Hold On")
                .artist("Adrian Sina")
                .title("Hold On")
                .genre("some genre")
                .releaseYear("2017")
                .trackNumber("1")
                .file(FileRequest.builder()
                        .fileType("mp3")
                        .partialChunkSize(128)
                        .build()
                )
                .build();

        AudioResponse audioResponse = audioService.postAudio(audio);

        String filePath = "/home/biscuit/Downloads/audiosplit/adrian sina - hold on.mp3";
        audioService.uploadAudioFile(audioResponse, filePath);

        byte[] buffer;

        audioResponse = audioService.getAudioByAudioId(audioResponse.getId());
        AudioBuffer audioBuffer = new AudioBuffer(audioResponse.getFile().getChunkNumber(), 524288);

        for (int chunkIndex = 0; chunkIndex < audioResponse.getFile().getChunkNumber(); chunkIndex++) {
            buffer = new byte[524288];

            audioService.downloadChunk(audioResponse.getId(), chunkIndex, buffer);
            audioBuffer.setChunk(chunkIndex, buffer);
        }
    }
}