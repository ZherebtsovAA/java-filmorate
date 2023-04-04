package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.exception.ValidateUserNameException;
import ru.yandex.practicum.filmorate.model.User;

public class UserNameValidator implements UserValidator {
    @Override
    public void validate(User user) throws ValidateException {
        if (user.getName().isBlank()) {
            throw new ValidateUserNameException("Имя не указано, будет использован логин");
        }
    }
}
