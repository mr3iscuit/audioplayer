package org.example;

import lombok.Getter;

import java.nio.ByteBuffer;

public class AudioBuffer {
    private final ByteBuffer buffer;

    @Getter
    private final int chunkSize;

    @Getter
    private int bufferSize;

    @Getter
    private boolean[] progress;

    AudioBuffer(int chunkNumber, int chunkSize) {
        this.progress = new boolean[chunkNumber];
        this.bufferSize = chunkNumber * chunkSize;
        this.buffer = ByteBuffer.allocate(bufferSize);
        this.chunkSize = chunkSize;
    }

    public synchronized void setChunk(Integer chunkIndex, byte[] chunk) {
        progress[chunkIndex] = true;
        for (boolean b : progress) {
            System.out.printf(" " + (b ? 1 : 0));
        }
        System.out.println();

        buffer.put(chunkSize * chunkIndex, chunk);
    }

    public synchronized ByteBuffer getSlice(int index, int bufferSize) {
        if (!progress[index / chunkSize]) {
            throw new ResourceIsNotReady("Chunk is not loaded");
        }
        return buffer.slice(index, bufferSize);
    }
}