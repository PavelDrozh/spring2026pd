package org.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity // Указывает, что данный класс является сущностью
@Table(name = "COMMENTS") // Задает имя таблицы, на которую будет отображаться сущность
@NamedEntityGraph(name = "books-entity-graph",
        attributeNodes = {@NamedAttributeNode("book")})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    Long id;
    @Column(name = "COMMENT", nullable = false)
    String comment;
    @JoinColumn(name = "BOOK_ID", nullable = false)
    @ManyToOne(targetEntity = Book.class)
    @JsonBackReference
    Book book;
}
