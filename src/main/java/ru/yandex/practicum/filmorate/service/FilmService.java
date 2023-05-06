package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film save(Film film) {
        if (film.getRate() == null) {
            film.setRate(0);
        }
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
            throw new NotFoundException("фильма с id{" + filmId + "} нет в списке фильмов");
        }
        return result.get();
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = findFilmById(filmId);
        User user = userService.findUserById(userId);
        filmStorage.addLike(film, user);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = findFilmById(filmId);
        User user = userService.findUserById(userId);
        filmStorage.deleteLike(film, user);
    }

    public List<Film> findPopularFilms(Integer count) {
        if (count < 0) {
            return Collections.emptyList();
        }
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> (-1) * film.getRate()))
                .limit(count)
                .collect(Collectors.toList());
    }

}