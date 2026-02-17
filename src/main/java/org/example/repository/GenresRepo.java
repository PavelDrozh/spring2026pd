package org.example.repository;

import org.example.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenresRepo extends JpaRepository<Genre, Long> {
}
