package org.example.shell;

import lombok.AllArgsConstructor;
import org.example.service.BooksService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@AllArgsConstructor
public class BooksCommands {

    private final BooksService booksService;

    @ShellMethod(key = "printAllBooks", value = "Print All Books")
    public void printAllBooks() {
        booksService.printAll();
    }

    @ShellMethod(key = "printBook", value = "Print Book")
    public void printBook() {
        booksService.printById();
    }

    @ShellMethod(key = "createBook", value = "Create Book")
    public void createBook() {
        booksService.create();
    }

    @ShellMethod(key = "updateBook", value = "Update Book")
    public void updateBook() {
        booksService.update();
    }

    @ShellMethod(key = "deleteBook", value = "Delete Book")
    public void deleteBook() {
        booksService.delete();
    }
}
