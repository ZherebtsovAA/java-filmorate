package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDb {
    private final JdbcTemplate jdbcTemplate;

    public boolean isNotExists(int genreId) {
        String sqlQuery = "SELECT count(*) FROM genre WHERE genre_id = ?";
        //noinspection ConstantConditions: return value is always an int, so NPE is impossible here
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, genreId);

        return result != 1;
    }

    public Genre findGenreById(Integer genreId) throws NotFoundException {
        if (isNotExists(genreId)) {
            throw new NotFoundException("записи о жанре с id{" + genreId + "} не найдено");
        }

        return jdbcTemplate.queryForObject(
                "SELECT genre_id, name FROM genre WHERE genre_id = ?",
                (resultSet, rowNum) -> Genre.builder()
                        .id(resultSet.getInt("GENRE_ID"))
                        .name(resultSet.getString("NAME"))
                        .build(), genreId);
    }

    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT * FROM genre", genreRowMapper);
    }

    private final RowMapper<Genre> genreRowMapper = (resultSet, rowNum) -> Genre.builder()
            .id(resultSet.getInt("GENRE_ID"))
            .name(resultSet.getString("NAME"))
            .build();

}