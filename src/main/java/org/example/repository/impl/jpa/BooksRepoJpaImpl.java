package org.example.repository.impl.jpa;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.example.model.Book;
import org.example.repository.BooksRepo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@AllArgsConstructor
public class BooksRepoJpaImpl implements BooksRepo {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Book> getAll() {
        EntityGraph<?> entityGraph = em.getEntityGraph("books-genre-author-entity-graph");
        TypedQuery<Book> query = em.createQuery("select distinct b from Book b", Book.class);
        query.setHint(FETCH.getKey(), entityGraph);
        return query.getResultList();
    }

    @Override
    public Optional<Book> getById(long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    @Override
    public Book create(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        return em.merge(book);
    }

    @Override
    public Book update(Book book) {
        return em.merge(book);
    }

    @Override
    public void delete(long id) {
        em.remove(em.find(Book.class, id));
    }
}
