package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.exception.ValidateUserLoginException;
import ru.yandex.practicum.filmorate.model.User;

public class UserLoginValidator implements UserValidator {
    @Override
    public void validate(User user) throws ValidateException {
        String login = user.getLogin().trim();
        if (login.isBlank() || login.contains(" ")) {
            throw new ValidateUserLoginException("Логин не может быть пустым и содержать пробелы");
        }
    }
}