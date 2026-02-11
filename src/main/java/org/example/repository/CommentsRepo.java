package org.example.repository;

import org.example.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentsRepo {
    List<Comment> getAllByBook(long bookId);
    Optional<Comment> getById(long id);
    Comment create(Comment comment);
    Comment update(Comment comment);
    void delete(long id);
    int deleteAllByBook(long bookId);
}
