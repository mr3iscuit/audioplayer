package org.example.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileResponse {

    private Long id;
    private Long fileSize;
    private String fileType;
    private Integer sampleRate;
    private Integer chunkNumber;
    private List<Long> chunksID;
}
