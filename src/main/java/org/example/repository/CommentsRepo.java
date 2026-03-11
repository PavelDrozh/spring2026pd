package org.example.repository;

import org.example.model.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "comments")
public interface CommentsRepo extends JpaRepository<Comment, Long> {

    @EntityGraph(value = "books-entity-graph") // или attributePaths = "book"
    List<Comment> findAllByBookId(Long bookId);

    long deleteByBookId(Long bookId);
}
