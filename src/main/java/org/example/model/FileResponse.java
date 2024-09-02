package org.example.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileResponse {
    private Long fileSize;
    private String fileType;
    private Integer sampleRate;
}
