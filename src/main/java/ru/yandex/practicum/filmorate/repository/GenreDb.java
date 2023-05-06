package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
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

    public Genre findGenreById(Integer genreId) throws NotFoundException {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT genre_id, name FROM genre WHERE genre_id = ?",
                    genreRowMapper, genreId);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("записи о жанре с id{" + genreId + "} не найдено");
        }
    }

    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT * FROM genre", genreRowMapper);
    }

    private final RowMapper<Genre> genreRowMapper = (resultSet, rowNum) -> Genre.builder()
            .id(resultSet.getInt("GENRE_ID"))
            .name(resultSet.getString("NAME"))
            .build();

}