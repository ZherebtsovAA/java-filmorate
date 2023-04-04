package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.exception.ValidateFilmDescriptionException;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmDescriptionValidator implements FilmValidator {
    @Override
    public void validate(Film film) throws ValidateException {
        if (film.getDescription().length() > 200) {
            throw new ValidateFilmDescriptionException("Максимальная длина описания фильма не более 200 символов");
        }
    }
}