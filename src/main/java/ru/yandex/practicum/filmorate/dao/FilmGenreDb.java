package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmGenreDb {
    private final JdbcTemplate jdbcTemplate;

    public Set<Genre> findGenreByFilmId(Integer filmId) {
        String sqlQuery = "SELECT g.genre_id, g.name " +
                "FROM film_genre AS fg " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, filmRowMapper, filmId));
    }

    private final RowMapper<Genre> filmRowMapper = (resultSet, rowNum) -> Genre.builder()
            .id(resultSet.getInt("GENRE_ID"))
            .name(resultSet.getString("NAME"))
            .build();

    public void update(int filmId, Set<Genre> genres) {
        //delete all genre for filmId
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);

        //insert genre for filmId
        for (Genre genre : genres) {
            jdbcTemplate.update(
                    "INSERT INTO film_genre (FILM_ID, GENRE_ID) values (?, ?)",
                    filmId, genre.getId());
        }
    }

}