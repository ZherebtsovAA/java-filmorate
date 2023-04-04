package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

public interface UserValidator {
    void validate(User user) throws ValidateException;
}