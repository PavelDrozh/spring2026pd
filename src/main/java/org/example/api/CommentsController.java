package org.example.api;

import org.example.dto.CommentDto;
import org.example.dto.DtoMapper;
import org.example.model.Book;
import org.example.model.Comment;
import org.example.service.CommentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public List<CommentDto> getAllByBook(@PathVariable long bookId) {
        return commentsService.getAllByBook(bookId).stream()
                .map(DtoMapper.INSTANCE::toCommentDto)
                .toList();
    }

    @PostMapping("/api/books/{bookId}/comments")
    public ResponseEntity<CommentDto> create(@PathVariable long bookId, @RequestBody Comment comment) {
        if (comment.getBook() == null) {
            comment.setBook(Book.builder().id(bookId).build());
        } else {
            comment.getBook().setId(bookId);
        }
        Comment created = commentsService.create(comment);
        return ResponseEntity.created(URI.create("/api/comments/" + created.getId())).body(DtoMapper.INSTANCE.toCommentDto(created));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/books/{bookId}/comments")
    public ResponseEntity<Void> deleteAllByBook(@PathVariable long bookId) {
        commentsService.deleteAllByBook(bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/comments/{id}")
    public CommentDto getById(@PathVariable long id) {
        return DtoMapper.INSTANCE.toCommentDto(commentsService.getById(id));
    }

    @PutMapping("/api/comments/{id}")
    public CommentDto update(@PathVariable long id, @RequestBody Comment comment) {
        Comment existing = commentsService.getById(id);
        comment.setId(existing.getId());
        if (comment.getBook() == null) {
            comment.setBook(existing.getBook());
        }
        if (comment.getComment() == null || comment.getComment().isBlank()) {
            comment.setComment(existing.getComment());
        }
        return DtoMapper.INSTANCE.toCommentDto(commentsService.update(comment));
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        commentsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
