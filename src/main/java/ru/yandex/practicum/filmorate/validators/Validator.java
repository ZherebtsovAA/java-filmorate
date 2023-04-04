package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exception.ValidateException;

public interface Validator {

    void validate(Object obj) throws ValidateException;
}
