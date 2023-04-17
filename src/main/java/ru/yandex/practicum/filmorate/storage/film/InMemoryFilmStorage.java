package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.function.BiFunction;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>(); // Key - film ID, Value - Film
    private final Map<Integer, HashSet<Integer>> ratingFilms = new HashMap<>(); // Key - film ID, Value - HashSet <like user ID>

    private final BiFunction<HashSet<Integer>, Integer, HashSet<Integer>> biFunction = (set, id) -> {
        set.add(id);
        return set;
    };

    public Film save(Film film) {
        films.put(film.getId(), film);
        ratingFilms.put(film.getId(), new HashSet<>());
        return films.get(film.getId());
    }

    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("в списке фильмов нет фильма с указанным ID: " + film.getId());
        }
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public void addLike(Integer filmId, Integer userId) {
        ratingFilms.put(filmId, biFunction.apply(ratingFilms.getOrDefault(filmId, new HashSet<>()), userId));
    }

    public void deleteLike(Integer filmId, Integer userId) {
        ratingFilms.get(filmId).remove(userId);
    }

    public List<Film> findFilmById(List<Integer> filmId) {
        if (filmId == null || filmId.size() == 0) {
            return Collections.emptyList();
        }

        List<Film> result = new ArrayList<>();
        for (Integer id : filmId) {
            if (films.get(id) != null) {
                result.add(films.get(id));
            }
        }

        if (result.size() == 0) {
            return Collections.emptyList();
        }

        return result;
    }

    public Map<Integer, HashSet<Integer>> getRatingFilms() {
        return new HashMap<>(ratingFilms);
    }

}