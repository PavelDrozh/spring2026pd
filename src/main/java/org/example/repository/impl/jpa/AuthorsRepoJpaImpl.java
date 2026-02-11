package org.example.repository.impl.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.example.model.Author;
import org.example.repository.AuthorsRepo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
@ConditionalOnProperty(value = "useJPA", havingValue = "true")
public class AuthorsRepoJpaImpl implements AuthorsRepo {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Author> getAll() {
        TypedQuery<Author> query = em.createQuery("select distinct a from Author a", Author.class);
        return query.getResultList();
    }

    @Override
    public Optional<Author> getById(long id) {
        return Optional.ofNullable(em.find(Author.class, id));
    }

    @Override
    public Author create(Author author) {
        if (author.getId() == null) {
            em.persist(author);
            return author;
        }
        return em.merge(author);
    }

    @Override
    public Author update(Author author) {
        return em.merge(author);
    }

    @Override
    public void delete(long id) {
        em.remove(em.find(Author.class, id));
    }
}
