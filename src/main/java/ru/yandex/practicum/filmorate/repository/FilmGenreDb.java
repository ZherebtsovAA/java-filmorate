package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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
        return new HashSet<>(jdbcTemplate.query(sqlQuery, genreRowMapper, filmId));
    }

    public void update(int filmId, Set<Genre> genres) {
        List<Integer> genresId = genres.stream().map(Genre::getId).collect(Collectors.toList());

        //delete all genre for filmId
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);

        //insert genre for filmId
        jdbcTemplate.batchUpdate(
                "INSERT INTO film_genre (FILM_ID, GENRE_ID) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, filmId);
                        ps.setInt(2, genresId.get(i));

                    }

                    @Override
                    public int getBatchSize() {
                        return genresId.size();
                    }
                }
        );
    }

    public Map<Integer, Set<Genre>> filmsIdByGenre() {
        Map<Integer, Set<Genre>> filmIdGenres = new HashMap<>();

        String sqlQuery = "SELECT fg.film_id, g.genre_id, g.name " +
                "FROM film_genre AS fg " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "ORDER BY fg.film_id";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery);

        for (Map<String, Object> row : rows) {
            Genre genre = Genre.builder()
                    .id((Integer) row.get("GENRE_ID"))
                    .name((String) row.get("NAME"))
                    .build();
            Integer filmId = (Integer) row.get("FILM_ID");
            Set<Genre> genres = filmIdGenres.getOrDefault(filmId, new HashSet<>());
            genres.add(genre);
            filmIdGenres.put(filmId, genres);
        }

        return filmIdGenres;
    }


    private final RowMapper<Genre> genreRowMapper = (resultSet, rowNum) -> Genre.builder()
            .id(resultSet.getInt("GENRE_ID"))
            .name(resultSet.getString("NAME"))
            .build();

}