package org.example.api;

import org.example.model.Book;
import org.example.model.Comment;
import org.example.service.CommentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class CommentsController {

    private final CommentsService commentsService;

    public CommentsController(CommentsService commentsService) {
        this.commentsService = commentsService;
    }

    @GetMapping("/api/books/{bookId}/comments")
    public List<Comment> getAllByBook(@PathVariable long bookId) {
        return commentsService.getAllByBook(bookId);
    }

    @PostMapping("/api/books/{bookId}/comments")
    public ResponseEntity<Comment> create(@PathVariable long bookId, @RequestBody Comment comment) {
        if (comment.getBook() == null) {
            comment.setBook(Book.builder().id(bookId).build());
        } else {
            comment.getBook().setId(bookId);
        }
        Comment created = commentsService.create(comment);
        return ResponseEntity.created(URI.create("/api/comments/" + created.getId())).body(created);
    }

    @DeleteMapping("/api/books/{bookId}/comments")
    public ResponseEntity<Void> deleteAllByBook(@PathVariable long bookId) {
        commentsService.deleteAllByBook(bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/comments/{id}")
    public Comment getById(@PathVariable long id) {
        return commentsService.getById(id);
    }

    @PutMapping("/api/comments/{id}")
    public Comment update(@PathVariable long id, @RequestBody Comment comment) {
        Comment existing = commentsService.getById(id);
        comment.setId(existing.getId());
        if (comment.getBook() == null) {
            comment.setBook(existing.getBook());
        }
        if (comment.getComment() == null || comment.getComment().isBlank()) {
            comment.setComment(existing.getComment());
        }
        return commentsService.update(comment);
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        commentsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
