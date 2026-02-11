package org.example.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Book {
    @EqualsAndHashCode.Exclude
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    @EqualsAndHashCode.Exclude
    private String description;
    @NotNull
    @EqualsAndHashCode.Exclude
    private LocalDate releaseDate;
    private Genre genre;
    private Author author;
}
