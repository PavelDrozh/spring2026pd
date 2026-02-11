package org.example.repository.impl.jpa;

import org.example.model.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(GenresRepoJpaImpl.class)
@TestPropertySource(properties = {
        "useJPA=true",
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class GenresRepoJpaImplTest {

    @Autowired
    private GenresRepoJpaImpl repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void getAll_returnsPersistedGenres() {
        em.persist(new Genre(null, "Comedy"));
        em.persist(new Genre(null, "Drama"));
        em.flush();
        em.clear();

        List<Genre> genres = repository.getAll();

        assertThat(genres)
                .hasSize(2)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Comedy", "Drama");
    }

    @Test
    void getById_returnsGenreWhenExists() {
        Genre saved = em.persistAndFlush(new Genre(null, "Thriller"));
        em.clear();

        var result = repository.getById(saved.getId());

        assertThat(result)
                .isPresent()
                .get()
                .extracting(Genre::getName)
                .isEqualTo("Thriller");
    }

    @Test
    void create_persistsWhenIdIsZero() {
        Genre created = repository.create(new Genre(null, "New"));
        em.flush();
        em.clear();

        Genre fromDb = em.find(Genre.class, created.getId());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo("New");
    }

    @Test
    void update_mergesChanges() {
        Genre saved = em.persistAndFlush(new Genre(null, "Old"));
        em.clear();

        saved.setName("Updated");
        repository.update(saved);
        em.flush();
        em.clear();

        Genre fromDb = em.find(Genre.class, saved.getId());
        assertThat(fromDb.getName()).isEqualTo("Updated");
    }

    @Test
    void delete_removesEntity() {
        Genre saved = em.persistAndFlush(new Genre(null, "ToDelete"));
        Long id = saved.getId();

        repository.delete(id);
        em.flush();
        em.clear();

        assertThat(em.find(Genre.class, id)).isNull();
    }
}
