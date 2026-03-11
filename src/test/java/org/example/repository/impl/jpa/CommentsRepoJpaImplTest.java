package org.example.repository.impl.jpa;

import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Comment;
import org.example.repository.CommentsRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CommentsRepoJpaImplTest {

    @Autowired
    private CommentsRepo repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void getAllByBook_returnsOnlyCommentsOfThatBook() {
        Author author1 = em.persist(new Author(null, "Name1", "Surname1", LocalDate.of(2000, 1, 1)));
        Author author2 = em.persist(new Author(null, "Name2", "Surname2", LocalDate.of(2001, 1, 1)));

        Book book1 = em.persist(Book.builder()
                .name("Book1")
                .description("Desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .genreId(1L)
                .author(author1)
                .build());

        Book book2 = em.persist(Book.builder()
                .name("Book2")
                .description("Desc")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .genreId(2L)
                .author(author2)
                .build());

        em.persist(Comment.builder().comment("c1").book(book1).build());
        em.persist(Comment.builder().comment("c2").book(book1).build());
        em.persist(Comment.builder().comment("c3").book(book2).build());
        em.flush();
        em.clear();

        var comments = repository.findAllByBookId(book1.getId());

        assertThat(comments)
                .hasSize(2)
                .extracting(Comment::getComment)
                .containsExactlyInAnyOrder("c1", "c2");
    }

    @Test
    void getById_returnsCommentWhenExists() {
        Author author = em.persist(new Author(null, "Name", "Surname", LocalDate.of(2000, 1, 1)));

        Book book = em.persist(Book.builder()
                .name("Book")
                .description("Desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .genreId(1L)
                .author(author)
                .build());

        Comment saved = em.persistAndFlush(Comment.builder().comment("hello").book(book).build());
        em.clear();

        var result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getComment()).isEqualTo("hello");
    }

    @Test
    void create_persistsWhenIdIsNull() {
        Author author = em.persist(new Author(null, "Name", "Surname", LocalDate.of(2000, 1, 1)));

        Book book = em.persist(Book.builder()
                .name("Book")
                .description("Desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .genreId(1L)
                .author(author)
                .build());

        Comment created = repository.save(Comment.builder().comment("new").book(book).build());
        em.flush();
        em.clear();

        Comment fromDb = em.find(Comment.class, created.getId());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getComment()).isEqualTo("new");
    }

    @Test
    void update_mergesChanges() {
        Author author = em.persist(new Author(null, "Name", "Surname", LocalDate.of(2000, 1, 1)));

        Book book = em.persist(Book.builder()
                .name("Book")
                .description("Desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .genreId(1L)
                .author(author)
                .build());

        Comment saved = em.persistAndFlush(Comment.builder().comment("old").book(book).build());
        em.clear();

        saved.setComment("updated");
        repository.save(saved);
        em.flush();
        em.clear();

        Comment fromDb = em.find(Comment.class, saved.getId());
        assertThat(fromDb.getComment()).isEqualTo("updated");
    }

    @Test
    void delete_removesEntity() {
        Author author = em.persist(new Author(null, "Name", "Surname", LocalDate.of(2000, 1, 1)));

        Book book = em.persist(Book.builder()
                .name("Book")
                .description("Desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .genreId(1L)
                .author(author)
                .build());

        Comment saved = em.persistAndFlush(Comment.builder().comment("toDelete").book(book).build());
        Long id = saved.getId();

        repository.deleteById(id);
        em.flush();
        em.clear();

        assertThat(em.find(Comment.class, id)).isNull();
    }

    @Test
    void deleteAllByBook_deletesAndReturnsCount() {
        Author author1 = em.persist(new Author(null, "Name1", "Surname1", LocalDate.of(2000, 1, 1)));
        Author author2 = em.persist(new Author(null, "Name2", "Surname2", LocalDate.of(2001, 1, 1)));

        Book book1 = em.persist(Book.builder()
                .name("Book1")
                .description("Desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .genreId(1L)
                .author(author1)
                .build());

        Book book2 = em.persist(Book.builder()
                .name("Book2")
                .description("Desc")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .genreId(2L)
                .author(author2)
                .build());

        em.persist(Comment.builder().comment("c1").book(book1).build());
        em.persist(Comment.builder().comment("c2").book(book1).build());
        em.persist(Comment.builder().comment("c3").book(book2).build());
        em.flush();
        em.clear();

        long deleted = repository.deleteByBookId(book1.getId());
        em.flush();
        em.clear();

        assertThat(deleted).isEqualTo(2);
        assertThat(repository.findAllByBookId(book1.getId())).isEmpty();
        assertThat(repository.findAllByBookId(book2.getId())).hasSize(1);
    }
}
