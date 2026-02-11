package org.example.repository.impl.jpa;

import org.example.model.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(AuthorsRepoJpaImpl.class)
@TestPropertySource(properties = {
        "useJPA=true",
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AuthorsRepoJpaImplTest {

    @Autowired
    private AuthorsRepoJpaImpl repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void getAll_returnsPersistedAuthors() {
        em.persist(new Author(null, "A", "A", LocalDate.of(2000, 1, 1)));
        em.persist(new Author(null, "B", "B", LocalDate.of(2001, 1, 1)));
        em.flush();
        em.clear();

        List<Author> authors = repository.getAll();

        assertThat(authors)
                .hasSize(2)
                .extracting(Author::getName)
                .containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void getById_returnsAuthorWhenExists() {
        Author saved = em.persistAndFlush(new Author(null, "John", "Doe", LocalDate.of(1980, 1, 15)));
        em.clear();

        var result = repository.getById(saved.getId());

        assertThat(result)
                .isPresent()
                .get()
                .extracting(Author::getSurname)
                .isEqualTo("Doe");
    }

    @Test
    void create_persistsWhenIdIsZero() {
        Author created = repository.create(new Author(null, "New", "Author", LocalDate.of(1999, 12, 31)));
        em.flush();
        em.clear();

        Author fromDb = em.find(Author.class, created.getId());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo("New");
    }

    @Test
    void update_mergesChanges() {
        Author saved = em.persistAndFlush(new Author(null, "Old", "Name", LocalDate.of(1970, 1, 1)));
        em.clear();

        saved.setName("Updated");
        repository.update(saved);
        em.flush();
        em.clear();

        Author fromDb = em.find(Author.class, saved.getId());
        assertThat(fromDb.getName()).isEqualTo("Updated");
    }

    @Test
    void delete_removesEntity() {
        Author saved = em.persistAndFlush(new Author(null, "ToDelete", "X", LocalDate.of(1970, 1, 1)));
        Long id = saved.getId();

        repository.delete(id);
        em.flush();
        em.clear();

        assertThat(em.find(Author.class, id)).isNull();
    }
}
