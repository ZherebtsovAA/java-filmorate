package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<String, User> users; //Key - email, Value - User
    private final List<UserValidator> userValidators = List.of(new UserEmailValidator(), new UserLoginValidator()
            , new UserNameValidator(), new UserBirthdayValidator());

    public UserController() {
        users = new HashMap<>();
    }

    @PostMapping
    public User add(@RequestBody User user, HttpServletRequest request) {
        try {
            log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Пользователь: '{}'"
                    , request.getMethod(), request.getRequestURI(), request.getQueryString(), user);
            checkValidatorRules(userValidators, user);
            users.put(user.getEmail(), user);
            return user;
        } catch (ValidateUserEmailException exception) {
            log.warn("Ошибка валидации почты: '{}'", exception.getMessage());
            System.out.println("Ошибка валидации почты: " + exception.getMessage());
            return user;
        } catch (ValidateUserLoginException exception) {
            log.warn("Ошибка валидации логина: '{}'", exception.getMessage());
            System.out.println("Ошибка валидации логина: " + exception.getMessage());
            return user;
        } catch (ValidateUserNameException exception) {
            log.warn("Ошибка валидации имени пользователя: '{}'", exception.getMessage());
            System.out.println("Ошибка валидации имени пользователя: " + exception.getMessage());
            user.setName(user.getLogin());
            users.put(user.getEmail(), user);
            return user;
        } catch (ValidateUserBirthdayException exception) {
            log.warn("Ошибка валидации дня рождения: '{}'", exception.getMessage());
            System.out.println("Ошибка валидации дня рождения: " + exception.getMessage());
            return user;
        } catch (ValidateException exception) {
            log.warn("Ошибка валидации: '{}'", exception.getMessage());
            System.out.println("Ошибка валидации: " + exception.getMessage());
            return user;
        }
    }

    @PutMapping
    public User update(@RequestBody User user, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Пользователь: '{}'"
                , request.getMethod(), request.getRequestURI(), request.getQueryString(), user);
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
