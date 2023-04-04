package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.exception.ValidateUserEmailException;
import ru.yandex.practicum.filmorate.model.User;

public class UserEmailValidator implements UserValidator {
    @Override
    public void validate(User user) throws ValidateException {
        String email = user.getEmail();
        if (email.isBlank() || !email.contains("@")) {
            throw new ValidateUserEmailException("Электронная почта не может быть пустой и должна содержать символ @");
        }
    }
}
