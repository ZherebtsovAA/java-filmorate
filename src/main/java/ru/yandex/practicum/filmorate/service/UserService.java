package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private static Integer globalUserId = 1;
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private static Integer getNextId() {
        return globalUserId++;
    }

    public User save(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("имя не указано, будет использован логин");
        }
        user.setId(getNextId());
        return userStorage.save(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findUserById(Integer userId) {
        List<User> result = userStorage.findUserById(new ArrayList<>(Collections.singletonList(userId)));
        if (result.isEmpty()) {
            throw new NotFoundException("пользователя с ID: " + userId + " нет в списке пользователей");
        }
        return result.get(0);
    }

    public void addToFriends(Integer userId, Integer friendId) {
        List<User> friend = userStorage.findUserById(new ArrayList<>(Collections.singletonList(friendId)));
        if (friend.isEmpty()) {
            throw new NotFoundException("пользователь с friendId = " + friendId + " отсутствует в списке пользователей");
        }
        userStorage.addToFriends(userId, friendId);
    }

    public void deleteToFriends(Integer userId, Integer friendId) {
        List<User> friend = userStorage.findUserById(new ArrayList<>(Collections.singletonList(friendId)));
        if (friend.isEmpty()) {
            throw new NotFoundException("пользователь с friendId = " + friendId + " отсутствует в списке пользователей");
        }
        userStorage.deleteToFriends(userId, friendId);
    }

    public List<User> findUserFriends(Integer userId) {
        return userStorage.findUserById(new ArrayList<>(userStorage.getIdUserFriends(userId)));
    }

    public List<User> findCommonFriends(Integer userId, Integer otherId) {
        HashSet<Integer> commonFriendsId = new HashSet<>();
        commonFriendsId.addAll(userStorage.getIdUserFriends(userId));
        commonFriendsId.addAll(userStorage.getIdUserFriends(otherId));
        commonFriendsId.remove(userId);
        commonFriendsId.remove(otherId);
        return userStorage.findUserById(new ArrayList<>(commonFriendsId));
    }

}