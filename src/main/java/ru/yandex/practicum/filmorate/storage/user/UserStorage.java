package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;

public interface UserStorage {
    User save(User user);

    User update(User user);

    List<User> findAll();

    void addToFriends(Integer userId, Integer friendId);

    void deleteToFriends(Integer userId, Integer friendId);

    List<User> findUserById(List<Integer> userId);

    HashSet<Integer> getIdUserFriends(Integer userId);
}