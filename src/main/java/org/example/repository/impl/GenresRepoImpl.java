package org.example.repository.impl;

import lombok.AllArgsConstructor;
import org.example.model.Genre;
import org.example.repository.GenresRepo;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class GenresRepoImpl implements GenresRepo {

    public static final String NAME_PARAM = "name";
    public static final String GENRE_ID_PARAM = "genreId";
    public static final String GENRE_ID = "GENRE_ID";
    public static final String GENRE = "GENRE";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM GENRES";
        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Long genreId = rs.getLong(GENRE_ID);
        String genreName = rs.getString(GENRE);
        return new Genre(genreId, genreName);
    }

    @Override
    public Optional<Genre> getById(long id) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID = :genreId";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue(GENRE_ID_PARAM, id);
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject
                (sql, namedParameters, (rs, rowNum) -> makeGenre(rs)));
    }

    @Override
    public Genre create(Genre genre) {
        String sqlQuery = "INSERT INTO GENRES (GENRE) " +
                "values (:name)";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue(NAME_PARAM, genre.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sqlQuery, namedParameters, keyHolder);
        genre.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        String sqlQuery = "UPDATE GENRES SET GENRE = :name " +
                "WHERE GENRE_ID = :genreId";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue(GENRE_ID_PARAM, genre.getId())
                .addValue(NAME_PARAM, genre.getName());
        namedParameterJdbcTemplate.update(sqlQuery, namedParameters);
        return getById(genre.getId()).orElse(null);
    }

    @Override
    public void delete(long id) {
        String sqlQuery = "DELETE FROM GENRES WHERE GENRE_ID = :genreId";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue(GENRE_ID_PARAM, id);
        namedParameterJdbcTemplate.update(sqlQuery, namedParameters);
    }
}
