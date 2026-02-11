package org.example.repository.impl.jdbc;

import lombok.AllArgsConstructor;
import org.example.model.Author;
import org.example.repository.AuthorsRepo;
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

@Repository
@AllArgsConstructor
@ConditionalOnProperty(value = "useJPA", havingValue = "false")
public class AuthorsRepoImpl implements AuthorsRepo {

    private static final String AUTHOR_ID_PARAM = "authorId";
    private static final String NAME_PARAM = "name";
    private static final String SURNAME_PARAM = "surname";
    private static final String BIRTHDAY_PARAM = "birthday";
    public static final String AUTHOR_ID = "AUTHOR_ID";
    public static final String NAME = "NAME";
    public static final String SURNAME = "SURNAME";
    public static final String BIRTHDAY = "BIRTHDAY";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Author> getAll() {
        String sql = "SELECT * FROM AUTHORS";
        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> makeAuthor(rs));
    }

    private Author makeAuthor(ResultSet rs) throws SQLException {
        Long authorId = rs.getLong(AUTHOR_ID);
        String authorName = rs.getString(NAME);
        String authorSurname = rs.getString(SURNAME);
        LocalDate authorBirthday = rs.getDate(BIRTHDAY).toLocalDate();
        return new Author(authorId, authorName, authorSurname, authorBirthday);
    }

    @Override
    public Optional<Author> getById(long id) {
        String sql = "SELECT * FROM AUTHORS WHERE AUTHOR_ID = :authorId";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue(AUTHOR_ID_PARAM, id);
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject
                (sql, namedParameters, (rs, rowNum) -> makeAuthor(rs)));
    }

    @Override
    public Author create(Author author) {
        String sqlQuery = "INSERT INTO AUTHORS (NAME, SURNAME, BIRTHDAY) " +
                "values (:name, :surname, :birthday)";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue(NAME_PARAM, author.getName())
                .addValue(SURNAME_PARAM, author.getSurname())
                .addValue(BIRTHDAY_PARAM, author.getBirthday());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sqlQuery, namedParameters, keyHolder);
        author.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return author;
    }

    @Override
    public Author update(Author author) {
        String sqlQuery = "UPDATE AUTHORS SET NAME = :name, SURNAME = :surname, BIRTHDAY = :birthday " +
                "WHERE AUTHOR_ID = :authorId";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue(AUTHOR_ID_PARAM, author.getId())
                .addValue(NAME_PARAM, author.getName())
                .addValue(SURNAME_PARAM, author.getSurname())
                .addValue(BIRTHDAY_PARAM, author.getBirthday());
        namedParameterJdbcTemplate.update(sqlQuery, namedParameters);
        return getById(author.getId()).orElse(null);
    }

    @Override
    public void delete(long id) {
        String sqlQuery = "DELETE FROM AUTHORS WHERE AUTHOR_ID = :authorId";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue(AUTHOR_ID_PARAM, id);
        namedParameterJdbcTemplate.update(sqlQuery, namedParameters);
    }
}
