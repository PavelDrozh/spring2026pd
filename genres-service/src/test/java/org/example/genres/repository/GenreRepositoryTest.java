package org.example.genres.repository;

import org.example.genres.model.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void saveAndFindById() {
        Genre saved = genreRepository.save(new Genre(null, "Comedy"));

        var found = genreRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Comedy");
    }

    @Test
    void findAllReturnsPersisted() {
        genreRepository.save(new Genre(null, "Drama"));
        genreRepository.save(new Genre(null, "Thriller"));

        var all = genreRepository.findAll();

        assertThat(all)
                .extracting(Genre::getName)
                .contains("Drama", "Thriller");
    }
}
