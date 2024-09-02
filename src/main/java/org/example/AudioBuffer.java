package org.example;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SharedBuffer {
    private final BlockingQueue<byte[]> bufferQueue = new LinkedBlockingQueue<>();
    private final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

    // Add chunk to buffer
    public synchronized void addChunk(byte[] chunk) {
        try {
            bufferQueue.put(chunk);  // Add to the queue
            byteStream.write(chunk); // Optionally keep track of all chunks
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get the next chunk for playing
    public byte[] getChunk() {
        try {
            return bufferQueue.take(); // Take from the queue
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    // Get the total downloaded data
    public synchronized byte[] getDownloadedData() {
        return byteStream.toByteArray();
    }

    // Check download progress
    public synchronized int getDownloadedSize() {
        return byteStream.size();
    }
}
