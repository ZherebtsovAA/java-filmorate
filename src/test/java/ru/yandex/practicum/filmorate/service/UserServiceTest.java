package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {
    private UserService userService = new UserService(new InMemoryUserStorage());

    @Test
    void findUserById() {
        User user = new User("email@ya.ru", "login", LocalDate.of(1984, 10, 10));
        Integer userId = userService.save(user).getId();

        assertEquals(userId, userService.findUserById(userId).getId());
        assertThrows(NotFoundException.class, () -> userService.findUserById(userId + 9999));
    }

    @Test
    void addToFriends() {
        List<User> users = new ArrayList<>(List.of(
                new User("email1@ya.ru", "login1", LocalDate.of(1984, 10, 10)),
                new User("email2@ya.ru", "login2", LocalDate.of(1984, 10, 10)),
                new User("email3@ya.ru", "login3", LocalDate.of(1984, 10, 10))));
        userService.save(users.get(0));
        userService.save(users.get(1));
        userService.save(users.get(2));

        assertEquals(0, userService.findUserById(users.get(0).getId()).getFriends().size());

        userService.addToFriends(users.get(0).getId(), users.get(0).getId());

        assertEquals(0, userService.findUserById(users.get(0).getId()).getFriends().size(),
                "Добавление пользователя к себе же в друзья");

        userService.addToFriends(users.get(0).getId(), users.get(1).getId());
        userService.addToFriends(users.get(0).getId(), users.get(2).getId());

        assertEquals(2, userService.findUserById(users.get(0).getId()).getFriends().size());
        assertThrows(NotFoundException.class, () -> userService.addToFriends(users.get(0).getId(), 9999));
        assertThrows(NotFoundException.class, () -> userService.addToFriends(9999, users.get(0).getId()));
        assertEquals(2, userService.findUserById(users.get(0).getId()).getFriends().size());
    }

    @Test
    void deleteToFriends() {
        List<User> users = new ArrayList<>(List.of(
                new User("email1@ya.ru", "login1", LocalDate.of(1984, 10, 10)),
                new User("email2@ya.ru", "login2", LocalDate.of(1984, 10, 10)),
                new User("email3@ya.ru", "login3", LocalDate.of(1984, 10, 10))));
        userService.save(users.get(0));
        userService.save(users.get(1));
        userService.save(users.get(2));

        userService.addToFriends(users.get(0).getId(), users.get(1).getId());
        userService.addToFriends(users.get(0).getId(), users.get(2).getId());

        assertEquals(2, userService.findUserById(users.get(0).getId()).getFriends().size());
        assertThrows(NotFoundException.class, () -> userService.addToFriends(users.get(0).getId(), 9999));
        assertThrows(NotFoundException.class, () -> userService.addToFriends(9999, users.get(0).getId()));
        assertEquals(2, userService.findUserById(users.get(0).getId()).getFriends().size());

        userService.deleteToFriends(users.get(0).getId(), users.get(1).getId());
        userService.deleteToFriends(users.get(0).getId(), users.get(2).getId());
        userService.deleteToFriends(users.get(0).getId(), users.get(2).getId());

        assertEquals(0, userService.findUserById(users.get(0).getId()).getFriends().size());
    }

    @Test
    void findUserFriends() {
        List<User> users = new ArrayList<>(List.of(
                new User("email1@ya.ru", "login1", LocalDate.of(1984, 10, 10)),
                new User("email2@ya.ru", "login2", LocalDate.of(1984, 10, 10)),
                new User("email3@ya.ru", "login3", LocalDate.of(1984, 10, 10))));
        userService.save(users.get(0));
        userService.save(users.get(1));
        userService.save(users.get(2));

        assertEquals(0, userService.findUserFriends(users.get(0).getId()).size());

        userService.addToFriends(users.get(0).getId(), users.get(1).getId());
        userService.addToFriends(users.get(0).getId(), users.get(2).getId());

        assertEquals(2, userService.findUserFriends(users.get(0).getId()).size());

        userService.deleteToFriends(users.get(0).getId(), users.get(1).getId());
        assertEquals(1, userService.findUserFriends(users.get(0).getId()).size());
    }

    @Test
    @Order(1)
    void findCommonFriends() {
        List<User> users = new ArrayList<>(List.of(
                new User("email1@ya.ru", "login1", LocalDate.of(1984, 10, 10)),
                new User("email2@ya.ru", "login2", LocalDate.of(1984, 10, 10)),
                new User("email3@ya.ru", "login3", LocalDate.of(1984, 10, 10)),
                new User("email4@ya.ru", "login4", LocalDate.of(1984, 10, 10)),
                new User("email5@ya.ru", "login5", LocalDate.of(1984, 10, 10))));
        userService.save(users.get(0));
        userService.save(users.get(1));
        userService.save(users.get(2));
        userService.save(users.get(3));
        userService.save(users.get(4));

        userService.addToFriends(users.get(0).getId(), users.get(1).getId());
        userService.addToFriends(users.get(0).getId(), users.get(2).getId());
        userService.addToFriends(users.get(0).getId(), users.get(3).getId());
        userService.addToFriends(users.get(0).getId(), users.get(4).getId());

        assertEquals(4, userService.findUserFriends(users.get(0).getId()).size());

        userService.addToFriends(users.get(4).getId(), users.get(0).getId());
        userService.addToFriends(users.get(4).getId(), users.get(1).getId());
        userService.addToFriends(users.get(4).getId(), users.get(2).getId());
        userService.addToFriends(users.get(4).getId(), users.get(3).getId());

        assertEquals(4, userService.findUserFriends(users.get(4).getId()).size());
        assertEquals(3, userService.findCommonFriends(users.get(0).getId(), users.get(4).getId()).size());

        List<Integer> friendsId = userService.findCommonFriends(users.get(0).getId(), users.get(4).getId()).stream()
                .map(x -> x.getId())
                .sorted()
                .collect(Collectors.toList());

        assertTrue(List.of(2, 3, 4).size() == friendsId.size()
                && List.of(2, 3, 4).containsAll(friendsId) && friendsId.containsAll(List.of(2, 3, 4)));
    }
}