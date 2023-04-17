package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>(); //Key - id, Value - User
    private int id = 1;

    @PostMapping
    public User add(@Valid @RequestBody User user, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Пользователь: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя не указано, будет использован логин");
        }
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}', Пользователь: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString(), user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            log.warn("Пользователя с ID: '" + user.getId() + "' нет в списке пользователей");
            throw new NotFoundException("Пользователя с ID: '" + user.getId() + "' нет в списке пользователей");
        }
        return user;
    }

    @GetMapping
    public List<User> allUsers() {
        return new ArrayList<>(users.values());
    }
}