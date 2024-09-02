package org.example.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AudioRequest {
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String releaseYear;
    private String trackNumber;
}
