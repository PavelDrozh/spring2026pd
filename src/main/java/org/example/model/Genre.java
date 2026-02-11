package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity // Указывает, что данный класс является сущностью
@Table(name = "GENRES") // Задает имя таблицы, на которую будет отображаться сущность
public class Genre {
    public Genre(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GENRE_ID")
    private Long id;
    @Column(name = "GENRE", length = 45, nullable = false)
    private String name;
}
