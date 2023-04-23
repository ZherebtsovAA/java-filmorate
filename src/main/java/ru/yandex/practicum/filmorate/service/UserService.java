package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User save(User user) {
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
            throw new NotFoundException("пользователя с ID: " + userId + " нет в списке пользователей");
        }
        return result.get();
    }

    public void addToFriends(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            return;
        }
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        // добавим userId и friendId в друзья друг другу
        user.setFriends(friendId);
        friend.setFriends(userId);
    }

    public void deleteToFriends(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            return;
        }
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        // удалим userId и friendId из друзьей друг друга
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> findUserFriends(Integer userId) {
        Set<Integer> friendsId = findUserById(userId).getFriends();
        List<User> friends = new ArrayList<>();
        for (Integer id : friendsId) {
            friends.add(findUserById(id));
        }
        return friends;
    }

    public List<User> findCommonFriends(Integer userId, Integer otherId) {
        Set<Integer> commonFriendsId = new HashSet<>(findUserById(userId).getFriends());
        commonFriendsId.retainAll(findUserById(otherId).getFriends());

        List<User> commonFriends = new ArrayList<>(commonFriendsId.size());
        for (Integer id : commonFriendsId) {
            commonFriends.add(findUserById(id));
        }

        return commonFriends;
    }
}