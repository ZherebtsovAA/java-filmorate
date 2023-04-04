package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.exception.ValidateFilmNameException;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmNameValidator implements FilmValidator {
    @Override
    public void validate(Film film) throws ValidateException {
        if (film.getName().isBlank()) {
            throw new ValidateFilmNameException("Название фильма не может быть пустым");
        }
    }
}
