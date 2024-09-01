package org.example;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AudioPostDTO {
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String releaseYear;
    private String trackNumber;
}
