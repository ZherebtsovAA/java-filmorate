package ru.yandex.practicum.filmorate.dao.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("inMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>(); // Key - film ID, Value - Film
    private Integer globalFilmId = 1;

    private Integer getNextId() {
        return globalFilmId++;
    }

    @Override
    public Film save(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        Integer filmId = film.getId();
        Optional<Film> result = findFilmById(filmId);
        if (result.isEmpty()) {
            String message = "фильм с ID: " + filmId + " не обновлен, нет в списке фильмов";
            log.warn(message);
            throw new NotFoundException(message);
        }
        films.put(filmId, film);
        return film;
    }

    @Override
    public Optional<Film> findFilmById(Integer filmId) {
        if (filmId == null || filmId < 1 || films.get(filmId) == null) {
            return Optional.empty();
        }
        return Optional.of(films.get(filmId));
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(Film film, User user) {
        film.setRate(film.getRate() + 1);
    }

    @Override
    public void deleteLike(Film film, User user) {
        if (film.getRate() < 1) {
            return;
        }
        film.setRate(film.getRate() - 1);
    }

}