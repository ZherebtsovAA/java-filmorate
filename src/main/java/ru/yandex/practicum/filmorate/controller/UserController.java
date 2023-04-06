package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users; //Key - id, Value - User
    private int id;
    private final List<UserValidator> userValidators = List.of(new UserEmailValidator(), new UserLoginValidator(),
            new UserNameValidator(), new UserBirthdayValidator());

    public UserController() {
        users = new HashMap<>();
        id = 1;
    }

    @PostMapping
    public User add(@Valid @RequestBody User user, HttpServletRequest request) {
        try {
            log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Пользователь: '{}'",
                    request.getMethod(), request.getRequestURI(), request.getQueryString(), user);
            checkValidatorRules(userValidators, user);
            user.setId(id);
            users.put(id++, user);
            return user;
        } catch (ValidateUserEmailException exception) {
            log.warn("Ошибка валидации почты: '{}'", exception.getMessage());
            return user;
        } catch (ValidateUserLoginException exception) {
            log.warn("Ошибка валидации логина: '{}'", exception.getMessage());
            return user;
        } catch (ValidateUserNameException exception) {
            log.warn("Ошибка валидации имени пользователя: '{}'", exception.getMessage());
            user.setName(user.getLogin());
            UserBirthdayValidator birthdayValidator = new UserBirthdayValidator();
            try {
                birthdayValidator.validate(user);
                user.setId(id);
                users.put(id++, user);
                return user;
            } catch (ValidateUserBirthdayException e) {
                log.warn("Ошибка валидации дня рождения: '{}'", e.getMessage());
                return user;
            } catch (ValidateException e) {
                log.warn("Ошибка валидации: '{}'", e.getMessage());
                return user;
            }
        } catch (ValidateUserBirthdayException exception) {
            log.warn("Ошибка валидации дня рождения: '{}'", exception.getMessage());
            return user;
        } catch (ValidateException exception) {
            log.warn("Ошибка валидации: '{}'", exception.getMessage());
            return user;
        }
    }

    @PutMapping
    public User update(@Valid @RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Пользователь: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            log.warn("Пользователя с ID: '" + user.getId() + "' нет в списке пользователей");
            response.setStatus(404);
        }
        return user;
    }

    @GetMapping
    public List<User> allUsers() {
        return new ArrayList<>(users.values());
    }

    private void checkValidatorRules(final List<UserValidator> validators, final User user) throws ValidateException {
        for (UserValidator validator : validators) {
            validator.validate(user);
        }
    }
}