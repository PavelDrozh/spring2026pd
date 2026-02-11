package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity // Указывает, что данный класс является сущностью
@Table(name = "AUTHORS") // Задает имя таблицы, на которую будет отображаться сущность
public class Author {
    public Author(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Long id;

    @Size(max = 30)
    @NotBlank
    @EqualsAndHashCode.Exclude
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 30)
    @NotBlank
    @EqualsAndHashCode.Exclude
    @Column(name = "surname", nullable = false)
    private String surname;

    @NotNull
    @EqualsAndHashCode.Exclude
    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;
}
