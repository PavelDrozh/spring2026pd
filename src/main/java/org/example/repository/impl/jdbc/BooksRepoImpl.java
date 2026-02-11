package org.example.repository.impl.jdbc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.repository.BooksRepo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.example.repository.impl.jdbc.AuthorsRepoImpl.*;
import static org.example.repository.impl.jdbc.GenresRepoImpl.GENRE;
import static org.example.repository.impl.jdbc.GenresRepoImpl.GENRE_ID;

@Slf4j
@Repository
@AllArgsConstructor
@ConditionalOnProperty(value = "useJPA", havingValue = "false")
public class BooksRepoImpl implements BooksRepo {
    private static final String BOOK_ID_PARAM = "book";
    private static final String NAME_PARAM = "name";
    private static final String DESCRIPTION_PARAM = "description";
    private static final String RELEASE_DATE_PARAM = "releaseDate";
    private static final String GENRE_ID_PARAM = "genreId";
    private static final String AUTHOR_ID_PARAM = "authorId";
    private static final String COLUMNS = "a.name aname, a.author_id, a.surname, a.birthday, b.book_id, b.name bname, b.description, b.release_date, g.genre, g.genre_id";
    private static final String BOOK_ID = "BOOK_ID";
    private static final String B_NAME = "bname";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String RELEASE_DATE = "RELEASE_DATE";
    private static final String A_NAME = "aname";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Book> getAll() {
        log.info("getAll");
        String sql = "SELECT " + COLUMNS + " FROM BOOKS b LEFT JOIN GENRES g on " +
                "b.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN AUTHORS a on b.AUTHOR_ID = a.AUTHOR_ID";
        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> makeBook(rs));
    }

    private Book makeBook(ResultSet rs) throws SQLException {
        long id = rs.getInt(BOOK_ID);
        String name = rs.getString(B_NAME);
        String description = rs.getString(DESCRIPTION);
        LocalDate releaseDate = rs.getDate(RELEASE_DATE).toLocalDate();
        Long genreId = rs.getLong(GENRE_ID);
        String genreName = rs.getString(GENRE);
        Long authorId = rs.getLong(AUTHOR_ID);
        String authorName = rs.getString(A_NAME);
        String authorSurname = rs.getString(SURNAME);
        LocalDate authorBirthday = rs.getDate(BIRTHDAY).toLocalDate();
        return new Book(id, name, description, releaseDate,
                new Genre(genreId, genreName),
                new Author(authorId, authorName, authorSurname, authorBirthday));
    }

    @Override
    public Optional<Book> getById(long id) {
        String sql = "SELECT " + COLUMNS + " FROM BOOKS b LEFT JOIN GENRES g on " +
                "b.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN AUTHORS a on b.AUTHOR_ID = a.AUTHOR_ID " +
                "where b.BOOK_ID = (:bookId)";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue(BOOK_ID_PARAM, id);
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject
                (sql, namedParameters, (rs, rowNum) -> makeBook(rs)));
    }

    @Override
    public Book create(Book book) {
        String sqlQuery = "INSERT INTO BOOKS (NAME, DESCRIPTION, RELEASE_DATE, GENRE_ID, AUTHOR_ID) " +
                "values (:name, :description, :releaseDate, :genreId, :authorId)";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue(NAME_PARAM, book.getName())
                .addValue(DESCRIPTION_PARAM, book.getDescription())
                .addValue(RELEASE_DATE_PARAM, book.getReleaseDate())
                .addValue(GENRE_ID_PARAM, book.getGenre().getId())
                .addValue(AUTHOR_ID_PARAM, book.getAuthor().getId());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sqlQuery, namedParameters, keyHolder);
        book.setId((Long) Objects.requireNonNull(Objects.requireNonNull(keyHolder.getKeys()).get(BOOK_ID)));
        return book;
    }

    @Override
    public Book update(Book book) {
        String sqlQuery = "UPDATE BOOKS SET NAME = :name, DESCRIPTION = :description, RELEASE_DATE = :releaseDate" +
                ", GENRE_ID = :genreId,  AUTHOR_ID = :authorId WHERE BOOK_ID = :bookId";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue(NAME_PARAM, book.getName())
                .addValue(DESCRIPTION_PARAM, book.getDescription())
                .addValue(RELEASE_DATE_PARAM, book.getReleaseDate())
                .addValue(GENRE_ID_PARAM, book.getGenre().getId())
                .addValue(AUTHOR_ID_PARAM, book.getAuthor().getId())
                .addValue(BOOK_ID_PARAM, book.getId());
        namedParameterJdbcTemplate.update(sqlQuery, namedParameters);
        return getById(book.getId()).orElse(null);
    }

    @Override
    public void delete(long id) {
        String sqlQuery = "DELETE FROM BOOKS WHERE BOOK_ID = :bookId";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue(BOOK_ID_PARAM, id);
        namedParameterJdbcTemplate.update(sqlQuery, namedParameters);
    }
}
