package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<String, Film> films; // Key - name, Value - Film

    public FilmController() {
        films = new HashMap<>();
    }

    @PostMapping
    public Film add(@RequestBody Film film) {
        films.put(film.getName(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        films.put(film.getName(), film);
        return film;
    }

    @GetMapping
    public List<Film> allFilms() {
        return new ArrayList<>(films.values());
    }


}
