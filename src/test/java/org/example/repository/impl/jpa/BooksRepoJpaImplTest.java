package org.example.repository.impl.jpa;

import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.repository.BooksRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BooksRepoJpaImplTest {

    @Autowired
    private BooksRepo repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void getAll_returnsPersistedBooks() {
        Genre genre1 = em.persist(new Genre(null, "Genre1"));
        Author author1 = em.persist(new Author(null, "Name1", "Surname1", LocalDate.of(2000, 1, 1)));
        Genre genre2 = em.persist(new Genre(null, "Genre2"));
        Author author2 = em.persist(new Author(null, "Name2", "Surname2", LocalDate.of(2001, 1, 1)));

        em.persist(Book.builder()
                .name("Book1")
                .description("Desc1")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .genre(genre1)
                .author(author1)
                .build());

        em.persist(Book.builder()
                .name("Book2")
                .description("Desc2")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .genre(genre2)
                .author(author2)
                .build());

        em.flush();
        em.clear();

        List<Book> books = repository.findAll();

        assertThat(books)
                .hasSize(2)
                .extracting(Book::getName)
                .containsExactlyInAnyOrder("Book1", "Book2");
    }

    @Test
    void getById_returnsBookWhenExists() {
        Genre genre = em.persist(new Genre(null, "Genre"));
        Author author = em.persist(new Author(null, "Name", "Surname", LocalDate.of(2000, 1, 1)));

        Book saved = em.persistAndFlush(Book.builder()
                .name("Book")
                .description("Desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .genre(genre)
                .author(author)
                .build());

        em.clear();

        var result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Book");
    }

    @Test
    void create_persistsWhenIdIsZero() {
        Genre genre = em.persist(new Genre(null, "Genre"));
        Author author = em.persist(new Author(null, "Name", "Surname", LocalDate.of(2000, 1, 1)));
        em.flush();

        Book created = repository.save(Book.builder()
                .name("NewBook")
                .description("NewDesc")
                .releaseDate(LocalDate.of(2022, 2, 2))
                .genre(genre)
                .author(author)
                .build());

        em.flush();
        em.clear();

        Book fromDb = em.find(Book.class, created.getId());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo("NewBook");
    }

    @Test
    void update_mergesChanges() {
        Genre genre = em.persist(new Genre(null, "Genre"));
        Author author = em.persist(new Author(null, "Name", "Surname", LocalDate.of(2000, 1, 1)));

        Book saved = em.persistAndFlush(Book.builder()
                .name("Old")
                .description("Desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .genre(genre)
                .author(author)
                .build());

        em.clear();

        saved.setName("Updated");
        repository.save(saved);
        em.flush();
        em.clear();

        Book fromDb = em.find(Book.class, saved.getId());
        assertThat(fromDb.getName()).isEqualTo("Updated");
    }

    @Test
    void delete_removesEntity() {
        Genre genre = em.persist(new Genre(null, "Genre"));
        Author author = em.persist(new Author(null, "Name", "Surname", LocalDate.of(2000, 1, 1)));

        Book saved = em.persistAndFlush(Book.builder()
                .name("ToDelete")
                .description("Desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .genre(genre)
                .author(author)
                .build());

        long id = saved.getId();

        repository.deleteById(id);
        em.flush();
        em.clear();

        assertThat(em.find(Book.class, id)).isNull();
    }
}
