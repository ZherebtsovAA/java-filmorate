package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film save(Film film) {
        return filmStorage.save(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilmById(Integer filmId) throws NotFoundException {
        Optional<Film> result = filmStorage.findFilmById(filmId);
        if (result.isEmpty()) {
            throw new NotFoundException("фильма с ID: " + filmId + " нет в списке фильмов");
        }
        return result.get();
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = findFilmById(filmId);
        userService.findUserById(userId);
        film.getRating().add(userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = findFilmById(filmId);
        userService.findUserById(userId);
        film.getRating().remove(userId);
    }

    public List<Film> findPopularFilms(Integer count) {
        if (count < 0) {
            return Collections.emptyList();
        }
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> (-1) * film.getRating().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}