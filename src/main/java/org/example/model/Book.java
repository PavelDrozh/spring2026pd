package org.example.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity // Указывает, что данный класс является сущностью
@Table(name = "BOOKS") // Задает имя таблицы, на которую будет отображаться сущность
@NamedEntityGraph(name = "books-author-entity-graph",
        attributeNodes = {@NamedAttributeNode("author")})
public class Book {

    public Book (long id, String name, String description, LocalDate releaseDate, Genre genre, Author author) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.genreId = genre == null ? null : genre.getId();
        this.author = author;
    }

    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @NotBlank
    @Size(max = 200)
    @EqualsAndHashCode.Exclude
    private String description;
    @NotNull
    @EqualsAndHashCode.Exclude
    private LocalDate releaseDate;
    @Column(name = "GENRE_ID", nullable = false)
    private Long genreId;
    @Transient
    private Genre genre;
    @OneToOne(targetEntity = Author.class)
    @JoinColumn(name = "AUTHOR_ID")
    private Author author;
    @OneToMany(targetEntity = Comment.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_ID")
    @JsonManagedReference
    private List<Comment> comments;
}
