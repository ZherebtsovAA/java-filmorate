package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmGenreDb;
import ru.yandex.practicum.filmorate.repository.MpaDb;

import java.util.*;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDb mpaDb;
    private final FilmGenreDb filmGenreDb;

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
        if (filmId == null || filmId < 1) {
            throw new NotFoundException("фильм с id{" + filmId + "} не обновлен, нет в списке фильмов");
        }

        String sqlQuery = "UPDATE film SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_id = ? " +
                "WHERE film_id = ?";

        int result = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                filmId);

        if (result == 0) {
            throw new NotFoundException("фильм с id{" + filmId + "} не обновлен, нет в списке фильмов");
        }

        if (film.getGenres() != null) {
            filmGenreDb.update(filmId, film.getGenres());
        }

        film.setMpa(mpaDb.findMpaById(film.getMpa().getId()));
        film.setGenres(filmGenreDb.findGenreByFilmId(film.getId()));

        return film;
    }

    @Override
    public Optional<Film> findFilmById(Integer filmId) {
        if (filmId == null || filmId < 1) {
            return Optional.empty();
        }

        try {
            Film film = jdbcTemplate.queryForObject(
                    "SELECT film_id, name, description, release_date, duration, rate, mpa_id FROM film WHERE film_id = ?",
                    filmRowMapper, filmId);

            //noinspection ConstantConditions
            film.setMpa(mpaDb.findMpaById(film.getMpa().getId()));
            film.setGenres(filmGenreDb.findGenreByFilmId(film.getId()));
            return Optional.of(film);

        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM film", filmRowMapper);

        List<Integer> mpaIds = films.stream()
                .map(film -> film.getMpa().getId())
                .distinct()
                .collect(Collectors.toList());
        Map<Integer, Mpa> mpaHash = mpaDb.findMpaByListId(mpaIds);

        Map<Integer, Set<Genre>> genreHash = filmGenreDb.filmsIdByGenre(); //filmId -> genres

        for (Film film : films) {
            film.setMpa(mpaHash.get(film.getMpa().getId()));
            film.setGenres(genreHash.getOrDefault(film.getId(), new HashSet<>()));
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