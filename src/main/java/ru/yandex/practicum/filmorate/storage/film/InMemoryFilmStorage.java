package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>(); // Key - film ID, Value - Film
    private static Integer globalFilmId = 1;

    private static Integer getNextId() {
        return globalFilmId++;
    }

    public Film save(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

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

    public Optional<Film> findFilmById(Integer filmId) {
        if (filmId == null || filmId < 1 || films.get(filmId) == null) {
            return Optional.empty();
        }
        return Optional.of(films.get(filmId));
    }

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }
}