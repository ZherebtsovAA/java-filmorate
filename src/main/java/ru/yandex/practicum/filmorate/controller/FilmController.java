package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<String, Film> films; // Key - name, Value - Film
    private final List<FilmValidator> filmValidators = List.of(new FilmNameValidator(), new FilmDescriptionValidator()
            , new FilmReleaseDateValidator(), new FilmDurationValidator());

    public FilmController() {
        films = new HashMap<>();
    }

    @PostMapping
    public Film add(@RequestBody Film film, HttpServletRequest request) {
        try {
            log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Фильм: '{}'"
                    , request.getMethod(), request.getRequestURI(), request.getQueryString(), film);
            checkValidatorRules(filmValidators, film);
            films.put(film.getName(), film);
            return film;
        } catch (ValidateFilmNameException exception) {
            log.warn("Ошибка валидации имени: '{}'", exception.getMessage());
            System.out.println("Ошибка валидации имени: " + exception.getMessage());
            return film;
        } catch (ValidateFilmDescriptionException exception) {
            log.warn("Ошибка валидации описания: '{}'", exception.getMessage());
            System.out.println("Ошибка валидации описания: " + exception.getMessage());
            return film;
        } catch (ValidateFilmReleaseDateException exception) {
            log.warn("Ошибка валидации даты релиза: '{}'", exception.getMessage());
            System.out.println("Ошибка валидации даты релиза: " + exception.getMessage());
            return film;
        } catch (ValidateFilmDurationException exception) {
            log.warn("Ошибка валидации продолжительности фильма: '{}'", exception.getMessage());
            System.out.println("Ошибка валидации продолжительности фильма: " + exception.getMessage());
            return film;
        } catch (ValidateException exception) {
            log.warn("Ошибка валидации: '{}'", exception.getMessage());
            System.out.println("Ошибка валидации: " + exception.getMessage());
            return film;
        }
    }

    @PutMapping
    public Film update(@RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Фильм: '{}'"
                , request.getMethod(), request.getRequestURI(), request.getQueryString(), film);
        films.put(film.getName(), film);
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