package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<String, User> users; //Key - email, Value - User

    public UserController() {
        users = new HashMap<>();
    }

    @PostMapping
    public User add(@RequestBody User user) {
        users.put(user.getEmail(), user);
        return user;
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

}
