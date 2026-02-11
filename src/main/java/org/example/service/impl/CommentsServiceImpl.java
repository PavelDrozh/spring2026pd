package org.example.service.impl;

import lombok.AllArgsConstructor;
import org.example.exceptions.CommentNotFoundException;
import org.example.model.Comment;
import org.example.repository.CommentsRepo;
import org.example.service.BooksService;
import org.example.service.CommentsService;
import org.example.util.LocalizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.example.util.LocalizationServiceImpl.COMMENTS_NOT_FOUND;

@Service
@AllArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepo commentsRepo;
    private final BooksService booksService;
    private final LocalizationService localizationService;

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllByBook(long bookId) {
        return commentsRepo.getAllByBook(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getById(long id) {
        Optional<Comment> oComment = commentsRepo.getById(id);
        if (oComment.isEmpty()) {
            throw new CommentNotFoundException(localizationService.getMessage(COMMENTS_NOT_FOUND, id));
        }
        return oComment.get();
    }

    @Override
    @Transactional
    public Comment create(Comment comment) {
        booksService.getById(comment.getBook().getId());
        return commentsRepo.create(comment);
    }

    @Override
    @Transactional
    public Comment update(Comment comment) {
        return commentsRepo.update(comment);
    }

    @Override
    @Transactional
    public void delete(long id) {
        commentsRepo.delete(id);
    }

    @Override
    @Transactional
    public void deleteAllByBook(long bookId) {
        commentsRepo.deleteAllByBook(bookId);
    }
}
