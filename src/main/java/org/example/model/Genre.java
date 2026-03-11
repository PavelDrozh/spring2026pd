package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    public Genre(Long id) {
        this.id = id;
    }

    private Long id;
    private String name;
}
