package org.example.api;

import jakarta.validation.Valid;
import org.example.dto.BookDto;
import org.example.dto.DtoMapper;
import org.example.model.Book;
import org.example.service.BooksService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BooksService booksService;

    public BookController(BooksService booksService) {
        this.booksService = booksService;
    }

    @GetMapping
    public List<BookDto> getAll() {
        return booksService.getAll().stream()
                .map(DtoMapper.INSTANCE::toBookDto)
                .toList();
    }

    @GetMapping("/{id}")
    public BookDto getById(@PathVariable long id) {
        return DtoMapper.INSTANCE.toBookDto(booksService.getById(id));
    }

    @PostMapping
    public ResponseEntity<BookDto> create(@Valid @RequestBody Book book) {
        Book created = booksService.create(book);
        return ResponseEntity.created(URI.create("/api/books/" + created.getId())).body(DtoMapper.INSTANCE.toBookDto(created));
    }

    @PutMapping("/{id}")
    public BookDto update(@PathVariable long id, @RequestBody Book book) {
        book.setId(id);
        return DtoMapper.INSTANCE.toBookDto(booksService.update(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        booksService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
