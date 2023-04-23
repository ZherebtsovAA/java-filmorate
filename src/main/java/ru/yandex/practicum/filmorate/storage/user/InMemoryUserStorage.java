package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>(); //Key - user ID, Value - User
    private Integer globalUserId = 1;

    private Integer getNextId() {
        return globalUserId++;
    }

    public User save(User user) {
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("имя не указано, будет использован логин");
        }
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        Integer userId = user.getId();
        Optional<User> result = findUserById(userId);
        if (result.isEmpty()) {
            String message = "пользователь с ID: " + userId + " не обновлен, нет в списке пользователей";
            log.warn(message);
            throw new NotFoundException(message);
        }
        users.put(userId, user);
        return user;
    }

    public Optional<User> findUserById(Integer userId) {
        if (userId == null || userId < 1 || users.get(userId) == null) {
            return Optional.empty();
        }
        return Optional.of(users.get(userId));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}