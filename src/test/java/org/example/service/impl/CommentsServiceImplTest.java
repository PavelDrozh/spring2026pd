package org.example.service.impl;

import org.example.exceptions.CommentNotFoundException;
import org.example.model.Book;
import org.example.model.Comment;
import org.example.repository.CommentsRepo;
import org.example.service.BooksService;
import org.example.util.LocalizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.example.util.LocalizationServiceImpl.COMMENTS_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentsServiceImplTest {

    private static final String MESSAGE = "message";

    @Mock
    private CommentsRepo commentsRepo;

    @Mock
    private BooksService booksService;

    @Mock
    private LocalizationService localizationService;

    private CommentsServiceImpl commentsService;

    private Book testBook;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        commentsService = new CommentsServiceImpl(commentsRepo, booksService, localizationService);
        testBook = new Book(1L, "Book", "Desc", LocalDate.of(2020, 1, 1), null, null);
        testComment = Comment.builder().id(1L).comment("hello").book(testBook).build();
    }

    @Test
    void getAllByBook_returnsFromRepo() {
        List<Comment> comments = List.of(testComment);
        when(commentsRepo.getAllByBook(1L)).thenReturn(comments);

        List<Comment> result = commentsService.getAllByBook(1L);

        verify(commentsRepo).getAllByBook(1L);
        assertIterableEquals(comments, result);
    }

    @Test
    void getById_returnsCommentWhenExists() {
        when(commentsRepo.getById(1L)).thenReturn(Optional.of(testComment));

        Comment result = commentsService.getById(1L);

        verify(commentsRepo).getById(1L);
        assertSame(testComment, result);
    }

    @Test
    void getById_throwsWhenNotFound() {
        when(commentsRepo.getById(999L)).thenReturn(Optional.empty());
        when(localizationService.getMessage(COMMENTS_NOT_FOUND, 999L)).thenReturn(MESSAGE);

        CommentNotFoundException ex = assertThrows(CommentNotFoundException.class, () -> commentsService.getById(999L));

        assertEquals(MESSAGE, ex.getMessage());
        verify(commentsRepo).getById(999L);
        verify(localizationService).getMessage(COMMENTS_NOT_FOUND, 999L);
        verifyNoMoreInteractions(commentsRepo, localizationService);
    }

    @Test
    void create_callsBookServiceValidationAndDelegatesToRepo() {
        Comment toCreate = Comment.builder().comment("new").book(testBook).build();
        Comment created = Comment.builder().id(10L).comment("new").book(testBook).build();

        when(booksService.getById(1L)).thenReturn(testBook);
        when(commentsRepo.create(toCreate)).thenReturn(created);

        Comment result = commentsService.create(toCreate);

        assertSame(created, result);
        verify(booksService).getById(1L);
        verify(commentsRepo).create(toCreate);
    }

    @Test
    void update_delegatesToRepo() {
        Comment updated = Comment.builder().id(1L).comment("updated").book(testBook).build();
        when(commentsRepo.update(updated)).thenReturn(updated);

        Comment result = commentsService.update(updated);

        assertSame(updated, result);
        verify(commentsRepo).update(updated);
    }

    @Test
    void delete_delegatesToRepo() {
        commentsService.delete(1L);

        verify(commentsRepo).delete(1L);
    }

    @Test
    void deleteAllByBook_delegatesToRepo() {
        when(commentsRepo.deleteAllByBook(1L)).thenReturn(2);

        commentsService.deleteAllByBook(1L);

        verify(commentsRepo).deleteAllByBook(1L);
    }
}
