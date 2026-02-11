package org.example.repository.impl.jpa;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.example.model.Comment;
import org.example.repository.CommentsRepo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@AllArgsConstructor
public class CommentsRepoJpaImpl implements CommentsRepo {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Comment> getAllByBook(long bookId) {
        EntityGraph<?> entityGraph = em.getEntityGraph("books-entity-graph");
        TypedQuery<Comment> query = em.createQuery(
                "select distinct c from Comment c where c.book.id = :bookId", Comment.class);
        query.setHint(FETCH.getKey(), entityGraph);
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }

    @Override
    public Optional<Comment> getById(long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public Comment create(Comment comment) {
        if (comment.getId() == null) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    public Comment update(Comment comment) {
        return em.merge(comment);
    }

    @Override
    public void delete(long id) {
        em.remove(em.find(Comment.class, id));
    }

    @Override
    public int deleteAllByBook(long bookId) {
        return em.createQuery("delete from Comment c where c.book.id = :bookId")
                .setParameter("bookId", bookId)
                .executeUpdate();
    }
}
