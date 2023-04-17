package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static Integer globalFilmId = 1;
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    private final Comparator<HashSet<Integer>> comparator = (countLike1, countLike2) -> countLike2.size() - countLike1.size();

    private static Integer getNextId() {
        return globalFilmId++;
    }

    public Film save(Film film) {
        film.setId(getNextId());
        return filmStorage.save(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilmById(Integer filmId) {
        List<Film> result = filmStorage.findFilmById(new ArrayList<>(Collections.singletonList(filmId)));
        if (result.isEmpty()) {
            throw new NotFoundException("фильма с ID: " + filmId + " нет в списке фильмов");
        }
        return result.get(0);
    }

    public void addLike(Integer filmId, Integer userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (userId <= 0) {
            throw new NotFoundException("передано отрицательное значение userId: " + userId);
        }
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> findPopularFilms(Integer count) {
        List<Integer> filmId = filmStorage.getRatingFilms().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(comparator))
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return filmStorage.findFilmById(filmId);
    }

}