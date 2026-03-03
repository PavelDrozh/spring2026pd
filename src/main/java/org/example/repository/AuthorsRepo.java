package org.example.repository;

import org.example.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorsRepo extends JpaRepository<Author, Long> {
}
