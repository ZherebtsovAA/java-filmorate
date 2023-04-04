package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<String, User> users; //Key - email, Value - User
    private final List<UserValidator> userValidators = List.of(new UserEmailValidator(), new UserLoginValidator()
            , new UserNameValidator(), new UserBirthdayValidator());

    public UserController() {
        users = new HashMap<>();
    }

    @PostMapping
    public User add(@RequestBody User user) {
        try {
            checkValidatorRules(userValidators, user);
            users.put(user.getEmail(), user);
            return user;
        } catch (ValidateUserEmailException exception) {
            System.out.println("Ошибка валидации почты: " + exception.getMessage());
            return user;
        } catch (ValidateUserLoginException exception) {
            System.out.println("Ошибка валидации логина: " + exception.getMessage());
            return user;
        } catch (ValidateUserNameException exception) {
            System.out.println("Ошибка валидации имени пользователя: " + exception.getMessage());
            user.setName(user.getLogin());
            users.put(user.getEmail(), user);
            return user;
        } catch (ValidateUserBirthdayException exception) {
            System.out.println("Ошибка валидации дня рождения: " + exception.getMessage());
            return user;
        } catch (ValidateException exception) {
            System.out.println("Ошибка валидации: " + exception.getMessage());
            return user;
        }
    }

    @PutMapping
    public User update(@RequestBody User user) {
        users.put(user.getEmail(), user);
        return user;
    }

    @GetMapping
    public List<User> allUsers() {
        return new ArrayList<>(users.values());
    }

    private void checkValidatorRules(final List<UserValidator> validators, final User user) throws ValidateException {
        for (UserValidator validator: validators) {
            validator.validate(user);
        }
    }
}
