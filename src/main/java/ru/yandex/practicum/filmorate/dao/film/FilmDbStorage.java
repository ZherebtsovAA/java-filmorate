package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmGenreDb;
import ru.yandex.practicum.filmorate.dao.MpaDb;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDb mpaDb;
    private final FilmGenreDb filmGenreDb;

    private boolean isNotExists(int filmId) {
        String sqlQuery = "SELECT count(*) FROM film WHERE film_id = ?";
        //noinspection ConstantConditions: return value is always an int, so NPE is impossible here
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId);

        return result != 1;
    }

    @Override
    public Film save(Film film) {
        film.setMpa(mpaDb.findMpaById(film.getMpa().getId()));

        SimpleJdbcInsert insertFilm = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILM")
                .usingGeneratedKeyColumns("FILM_ID");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("NAME", film.getName())
                .addValue("DESCRIPTION", film.getDescription())
                .addValue("RELEASE_DATE", film.getReleaseDate())
                .addValue("DURATION", film.getDuration())
                .addValue("RATE", film.getRate())
                .addValue("MPA_ID", film.getMpa().getId());

        Number filmId = insertFilm.executeAndReturnKey(parameters);
        film.setId(filmId.intValue());

        if (film.getGenres() != null) {
            filmGenreDb.update(filmId.intValue(), film.getGenres());
        }
        film.setGenres(filmGenreDb.findGenreByFilmId(film.getId()));

        return film;
    }

    @Override
    public Film update(Film film) {
        Integer filmId = film.getId();
        if (filmId == null || filmId < 1 || isNotExists(filmId)) {
            throw new NotFoundException("фильм с id{" + filmId + "} не обновлен, нет в списке фильмов");
        }

        String sqlQuery = "UPDATE film SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                filmId);

        if (film.getGenres() != null) {
            filmGenreDb.update(filmId, film.getGenres());
        }

        film.setMpa(mpaDb.findMpaById(film.getMpa().getId()));
        film.setGenres(filmGenreDb.findGenreByFilmId(film.getId()));

        return film;
    }

    @Override
    public Optional<Film> findFilmById(Integer filmId) {
        if (filmId == null || filmId < 1 || isNotExists(filmId)) {
            return Optional.empty();
        }

        Film film = jdbcTemplate.queryForObject(
                "SELECT film_id, name, description, release_date, duration, rate, mpa_id FROM film WHERE film_id = ?",
                (resultSet, rowNum) -> Film.builder()
                        .id(resultSet.getInt("FILM_ID"))
                        .name(resultSet.getString("NAME"))
                        .description(resultSet.getString("DESCRIPTION"))
                        .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                        .duration(resultSet.getInt("DURATION"))
                        .rate(resultSet.getInt("RATE"))
                        .mpa(Mpa.builder().id(resultSet.getInt("MPA_ID")).build())
                        .build(), filmId);

        //noinspection ConstantConditions: наличие film проверено методом isNotExists(filmId)
        film.setMpa(mpaDb.findMpaById(film.getMpa().getId()));
        film.setGenres(filmGenreDb.findGenreByFilmId(film.getId()));

        return Optional.of(film);
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM film", filmRowMapper);
        for (Film film : films) {
            film.setMpa(mpaDb.findMpaById(film.getMpa().getId()));
            film.setGenres(filmGenreDb.findGenreByFilmId(film.getId()));
        }

        return films;
    }

    private final RowMapper<Film> filmRowMapper = (resultSet, rowNum) -> Film.builder()
            .id(resultSet.getInt("FILM_ID"))
            .name(resultSet.getString("NAME"))
            .description(resultSet.getString("DESCRIPTION"))
            .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
            .duration(resultSet.getInt("DURATION"))
            .rate(resultSet.getInt("RATE"))
            .mpa(Mpa.builder().id(resultSet.getInt("MPA_ID")).build())
            .build();

    @Override
    public void addLike(Film film, User user) {
        String sqlQuery = "UPDATE film SET rate = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getRate() + 1,
                film.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        if (film.getRate() < 1) {
            return;
        }
        String sqlQuery = "UPDATE film SET rate = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getRate() - 1,
                film.getId());
    }

}