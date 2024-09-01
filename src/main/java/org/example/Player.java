package org.example;

import lombok.Getter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Player implements Runnable {
    Thread thread;
    AudioFormat format;
    AudioBuffer buffer;

    Player(AudioFormat format, AudioBuffer buffer) {
        this.buffer = buffer;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        playFromBuffer();
    }
    private void playFromBuffer() {
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            line.write(buffer.getBytes(), 0, buffer.getChunkSize());

            line.drain();
            line.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
