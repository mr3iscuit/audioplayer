package org.example;

import lombok.Getter;
import lombok.Setter;

public class AudioChunk {
    @Setter
    @Getter
    byte[] chunk;

    @Getter
    Long chunkSize;
}
