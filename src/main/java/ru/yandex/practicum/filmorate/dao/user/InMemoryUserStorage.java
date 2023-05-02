package ru.yandex.practicum.filmorate.dao.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>(); //Key - user ID, Value - User
    private Integer globalUserId = 1;

    private Integer getNextId() {
        return globalUserId++;
    }

    @Override
    public User save(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        Integer userId = user.getId();
        Optional<User> result = findUserById(userId);
        if (result.isEmpty()) {
            String message = "пользователь с id{" + userId + "} не обновлен, нет в списке пользователей";
            log.warn(message);
            throw new NotFoundException(message);
        }
        users.put(userId, user);
        return user;
    }

    @Override
    public Optional<User> findUserById(Integer userId) {
        if (userId == null || userId < 1 || users.get(userId) == null) {
            return Optional.empty();
        }
        return Optional.of(users.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addToFriends(User user, User friend) {
        user.getFriends().add(friend.getId());
    }

    @Override
    public void deleteToFriends(User user, User friend) {
        user.getFriends().remove(friend.getId());
    }

    @Override
    public List<User> findUserFriends(Integer userId) {
        Set<Integer> friendsId = users.get(userId).getFriends();
        List<User> friends = new ArrayList<>();
        for (Integer id : friendsId) {
            findUserById(id).ifPresent(friends::add);
        }

        return friends;
    }

    @Override
    public List<User> findCommonFriends(Integer userId, Integer otherId) {
        Set<Integer> userFriendsId = users.get(userId).getFriends();
        if (userFriendsId == null || userFriendsId.size() == 0) {
            return Collections.emptyList();
        }

        Set<Integer> commonFriendsId = new HashSet<>(userFriendsId);

        Set<Integer> otherFriendsId = users.get(otherId).getFriends();
        if (otherFriendsId == null || otherFriendsId.size() == 0) {
            return Collections.emptyList();
        }

        commonFriendsId.retainAll(otherFriendsId);

        List<User> commonFriends = new ArrayList<>(commonFriendsId.size());
        for (Integer id : commonFriendsId) {
            findUserById(id).ifPresent(commonFriends::add);
        }

        return commonFriends;
    }

}