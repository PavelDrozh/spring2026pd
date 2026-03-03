package org.example.shell;

import lombok.AllArgsConstructor;
import org.example.model.Book;
import org.example.model.Comment;
import org.example.service.CommentsService;
import org.example.util.LocalizedIOservice;
import org.example.util.UsersCommandGetter;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;

import static org.example.util.LocalizationServiceImpl.*;

@ShellComponent
@AllArgsConstructor
public class CommentsCommands {

    private final CommentsService commentsService;
    private final LocalizedIOservice ioService;
    private final UsersCommandGetter commandGetter;


    @ShellMethod(key = "printBooksComments", value = "Print All Books Comments")
    public void getAllByBook() {
        ioService.printMessage(COMMENTS_GET_BOOK);
        long bookId = commandGetter.getIdFromUser(BOOKS_FIND_BY_ID_ERR);
        List<Comment> comments = commentsService.getAllByBook(bookId);
        for (Comment c : comments) {
            printComment(c);
            ioService.printString("=============");
        }
    }

    @ShellMethod(key = "printBooksCommentById", value = "Print Books Comment by id")
    public void getById() {
        ioService.printMessage(COMMENTS_GET_COMMENT_ID);
        long commentId = commandGetter.getIdFromUser(COMMENTS_PARSE_ID_ERR);
        Comment comment = commentsService.getById(commentId);
        printComment(comment);
    }

    @ShellMethod(key = "createComment", value = "Create Comment")
    public void create() {
        ioService.printMessage(COMMENTS_GET_BOOK);
        long bookId = commandGetter.getIdFromUser(BOOKS_FIND_BY_ID_ERR);
        ioService.printMessage(COMMENTS_GET_COMMENT);
        String comment = commandGetter.getNotEmptyString();
        Comment result = commentsService.create(Comment.builder()
                        .id(null)
                        .book(Book.builder().id(bookId).build())
                        .comment(comment)
                        .build());
        printComment(result);
    }

    @ShellMethod(key = "updateComment", value = "Update Comment")
    public void update() {
        ioService.printMessage(COMMENTS_GET_COMMENT_ID);
        long commentId = commandGetter.getIdFromUser(COMMENTS_PARSE_ID_ERR);
        ioService.printMessage(COMMENTS_GET_COMMENT);
        String comment = commandGetter.getNotEmptyString();
        Comment commentForUpdate = commentsService.getById(commentId);
        commentForUpdate.setComment(comment);
        Comment result = commentsService.update(commentForUpdate);
        printComment(result);
    }

    @ShellMethod(key = "deleteComment", value = "Delete Comment")
    public void delete() {
        ioService.printMessage(COMMENTS_GET_COMMENT_ID);
        long commentId = commandGetter.getIdFromUser(COMMENTS_PARSE_ID_ERR);
        commentsService.delete(commentId);
    }

    @ShellMethod(key = "deleteBooksComments", value = "Delete Books Comments")
    public void deleteAllByBook() {
        ioService.printMessage(COMMENTS_GET_BOOK_DELETE);
        long bookId = commandGetter.getIdFromUser(BOOKS_FIND_BY_ID_ERR);
        commentsService.deleteAllByBook(bookId);
    }

    private void printComment(Comment c) {
        ioService.printMessage(COMMENTS_ID,c.getId());
        ioService.printMessage(COMMENTS_BOOK_ID,c.getBook().getId());
        ioService.printMessage(COMMENTS_COMMENT,c.getComment());
    }

}
