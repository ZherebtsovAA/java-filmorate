package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<String, Film> films; // Key - name, Value - Film
    private final List<FilmValidator> filmValidators = List.of(new FilmNameValidator(), new FilmDescriptionValidator()
            , new FilmReleaseDateValidator(), new FilmDurationValidator());

    public FilmController() {
        films = new HashMap<>();
    }

    @PostMapping
    public Film add(@RequestBody Film film) {
        try {
            checkValidatorRules(filmValidators, film);
            films.put(film.getName(), film);
            return film;
        } catch (ValidateFilmNameException exception) {
            System.out.println("Ошибка валидации имени: " + exception.getMessage());
            return film;
        } catch (ValidateFilmDescriptionException exception) {
            System.out.println("Ошибка валидации описания: " + exception.getMessage());
            return film;
        } catch (ValidateFilmReleaseDateException exception) {
            System.out.println("Ошибка валидации даты релиза: " + exception.getMessage());
            return film;
        } catch (ValidateFilmDurationException exception) {
            System.out.println("Ошибка валидации продолжительности фильма: " + exception.getMessage());
            return film;
        } catch (ValidateException exception) {
            System.out.println("Ошибка валидации: " + exception.getMessage());
            return film;
        }
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

    private void checkValidatorRules(final List<FilmValidator> validators, final Film film) throws ValidateException {
        for (FilmValidator validator: validators) {
            validator.validate(film);
        }
    }
}
