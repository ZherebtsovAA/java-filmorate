package ru.yandex.practicum.filmorate.dao.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User save(User user);

    User update(User user);

    Optional<User> findUserById(Integer userId);

    List<User> findAll();

    void addToFriends(User user, User friend);

    void deleteToFriends(User user, User friend);

    List<User> findUserFriends(Integer userId);

    List<User> findCommonFriends(Integer userId, Integer otherId);

}