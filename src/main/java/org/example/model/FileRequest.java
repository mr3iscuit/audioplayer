package org.example.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Data
@Getter
@Setter
public class FileRequest {
    private Long fileSize;
    private String fileType;
    private Long sampleRate;
    private Integer partialChunkSize;
}
