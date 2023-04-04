package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.exception.ValidateFilmDurationException;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmDurationValidator implements FilmValidator {
    @Override
    public void validate(Film film) throws ValidateException {
        if (film.getDuration() < 0) {
            throw new ValidateFilmDurationException("Продолжительность фильма должна быть положительной");
        }
    }
}