package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films; // Key - id, Value - Film
    private int id;
    private final List<FilmValidator> filmValidators = List.of(new FilmNameValidator(), new FilmDescriptionValidator()
            , new FilmReleaseDateValidator(), new FilmDurationValidator());

    public FilmController() {
        films = new HashMap<>();
        id = 1;
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film, HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Фильм: '{}'"
                    , request.getMethod(), request.getRequestURI(), request.getQueryString(), film);
            checkValidatorRules(filmValidators, film);
            final Film newFilm = film.toBuilder().id(id).build();
            films.put(id++, newFilm);
            return newFilm;
        } catch (ValidateFilmNameException exception) {
            log.warn("Ошибка валидации имени: '{}'", exception.getMessage());
            response.setStatus(400);
            return film;
        } catch (ValidateFilmDescriptionException exception) {
            log.warn("Ошибка валидации описания: '{}'", exception.getMessage());
            response.setStatus(400);
            return film;
        } catch (ValidateFilmReleaseDateException exception) {
            log.warn("Ошибка валидации даты релиза: '{}'", exception.getMessage());
            response.setStatus(400);
            return film;
        } catch (ValidateFilmDurationException exception) {
            log.warn("Ошибка валидации продолжительности фильма: '{}'", exception.getMessage());
            response.setStatus(400);
            return film;
        } catch (ValidateException exception) {
            log.warn("Ошибка валидации: '{}'", exception.getMessage());
            response.setStatus(400);
            return film;
        }
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film, HttpServletRequest request, HttpServletResponse response) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Фильм: '{}'"
                , request.getMethod(), request.getRequestURI(), request.getQueryString(), film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        }
        response.setStatus(404);
        return film;
    }

    @GetMapping
    public List<Film> allFilms() {
        return new ArrayList<>(films.values());
    }

    private void checkValidatorRules(final List<FilmValidator> validators, final Film film) throws ValidateException {
        for (FilmValidator validator : validators) {
            validator.validate(film);
        }
    }
}