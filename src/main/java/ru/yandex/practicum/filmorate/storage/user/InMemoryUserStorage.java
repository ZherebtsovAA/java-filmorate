package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.function.BiFunction;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>(); //Key - user ID, Value - User
    private final Map<Integer, HashSet<Integer>> userFriends = new HashMap<>(); // Key - user ID, Value - Set<friend ID>

    private final BiFunction<HashSet<Integer>, Integer, HashSet<Integer>> biFunction = (set, id) -> {
        set.add(id);
        return set;
    };

    public User save(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new NotFoundException("пользователя с ID: " + user.getId() + " нет в списке пользователей");
        }
        return users.get(user.getId());
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void addToFriends(Integer userId, Integer friendId) {
        // добавим userId и friendId в друзья друг другу
        userFriends.put(userId, biFunction.apply(
                userFriends.getOrDefault(userId, new HashSet<>()),
                friendId)
        );
        userFriends.put(friendId, biFunction.apply(
                userFriends.getOrDefault(friendId, new HashSet<>()),
                userId)
        );
    }

    public void deleteToFriends(Integer userId, Integer friendId) {
        // удалим userId и friendId из друзьей друг друга
        userFriends.get(userId).remove(friendId);
        userFriends.get(friendId).remove(userId);
    }

    public List<User> findUserById(List<Integer> userId) {
        if (userId == null || userId.size() == 0) {
            return Collections.emptyList();
        }

        List<User> result = new ArrayList<>();
        for (Integer id : userId) {
            if (users.get(id) != null) {
                result.add(users.get(id));
            }
        }

        if (result.size() == 0) {
            return Collections.emptyList();
        }

        return result;
    }

    public HashSet<Integer> getIdUserFriends(Integer userId) {
        if (userFriends.get(userId) == null) {
            return new HashSet<>();
        }
        return new HashSet<>(userFriends.get(userId));
    }

}