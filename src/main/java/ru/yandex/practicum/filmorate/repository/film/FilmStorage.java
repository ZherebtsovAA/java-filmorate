package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film film);

    Optional<Film> findFilmById(Integer filmId);

    List<Film> findAll();

    void addLike(Film film, User user);

    void deleteLike(Film film, User user);
}