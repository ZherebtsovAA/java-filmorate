package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public User save(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.save(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findUserById(Integer userId) throws NotFoundException {
        Optional<User> result = userStorage.findUserById(userId);
        if (result.isEmpty()) {
            throw new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей");
        }
        return result.get();
    }

    public void addToFriends(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            return;
        }
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        userStorage.addToFriends(user, friend);
    }

    public void deleteToFriends(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            return;
        }
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        userStorage.deleteToFriends(user, friend);
    }

    public List<User> findUserFriends(Integer userId) {
        findUserById(userId);
        return userStorage.findUserFriends(userId);
    }

    public List<User> findCommonFriends(Integer userId, Integer otherId) {
        findUserById(userId);
        findUserById(otherId);
        return userStorage.findCommonFriends(userId, otherId);
    }
}