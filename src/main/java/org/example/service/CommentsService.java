package org.example.service;

import org.example.model.Comment;

import java.util.List;

public interface CommentsService {
    List<Comment> getAllByBook(long bookId);
    Comment getById(long id);
    Comment create(Comment comment);
    Comment update(Comment comment);
    void delete(long id);
    void deleteAllByBook(long bookId);
}
