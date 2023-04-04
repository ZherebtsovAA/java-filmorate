package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.exception.ValidateFilmReleaseDateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmReleaseDateValidator implements FilmValidator {
    @Override
    public void validate(Film film) throws ValidateException {
        LocalDate birthdayCinema = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(birthdayCinema)) {
            throw new ValidateFilmReleaseDateException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
