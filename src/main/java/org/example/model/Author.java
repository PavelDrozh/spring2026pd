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
public class Author {
    @NotNull
    private Long id;

    @Size(max = 30)
    @NotBlank
    @EqualsAndHashCode.Exclude
    private String name;

    @Size(max = 30)
    @NotBlank
    @EqualsAndHashCode.Exclude
    private String surname;

    @NotNull
    @EqualsAndHashCode.Exclude
    private LocalDate birthday;
}
