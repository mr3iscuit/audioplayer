package org.example;

import lombok.Data;
import lombok.Getter;

@Data
public class AudioBuffer {

    @Getter byte[] bytes;
    @Getter Integer chunkSize;
    @Getter Long bufferSize;

    AudioBuffer(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }

    public void setChunk(Integer chunkIndex, AudioChunk chunk) {
        int bytesStartIndex = chunkIndex * chunkSize;

        for (int index = 0; index < bufferSize; index++) {
            bytes[bytesStartIndex] = chunk.getChunk()[index];
        }
    }
}
