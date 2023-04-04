package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmValidator {
    void validate(Film film) throws ValidateException;
}