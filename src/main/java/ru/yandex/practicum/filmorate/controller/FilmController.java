package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>(); // Key - id, Value - Film
    private int id = 1;

    @PostMapping
    public Film add(@Valid @RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Фильм: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), film);
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Фильм: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            log.warn("Фильма с ID: '" + film.getId() + "' нет в списке фильмов");
            throw new NotFoundException("Фильма с ID: '" + film.getId() + "' нет в списке фильмов");
        }
        return film;
    }

    @GetMapping
    public List<Film> allFilms() {
        return new ArrayList<>(films.values());
    }
}