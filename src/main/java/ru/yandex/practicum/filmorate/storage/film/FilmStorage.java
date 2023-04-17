package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film film);

    List<Film> findAll();

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    List<Film> findFilmById(List<Integer> filmId);

    Map<Integer, HashSet<Integer>> getRatingFilms();
}