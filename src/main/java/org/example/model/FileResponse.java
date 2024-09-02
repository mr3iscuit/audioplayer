package org.example.model;

import lombok.*;

import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    private Long fileSize;
    private String fileType;
    private Integer sampleRate;

    // Getters and Setters
    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }
}
