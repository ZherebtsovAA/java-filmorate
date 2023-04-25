package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {
    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

    @Test
    void save() {
        User user = new User("email@ya.ru", "login", LocalDate.of(1984, 10, 10));

        assertNull(user.getId());
        assertNull(user.getName());

        user = inMemoryUserStorage.save(user);

        assertEquals(1, user.getId());
        assertEquals("login", user.getName());
    }

    @Test
    void update() {
        User user = new User("email@ya.ru", "login", LocalDate.of(1984, 10, 10));
        Integer userId = inMemoryUserStorage.save(user).getId();
        User userUpdate = new User("emailUpdate@ya.ru", "loginUpdate", LocalDate.of(1984, 10, 10));
        userUpdate.setId(userId);
        inMemoryUserStorage.update(userUpdate);

        assertNotEquals(user.getName(), userUpdate.getName());

        user.setId(userId + 9999);
        assertThrows(NotFoundException.class, () -> inMemoryUserStorage.update(user));
    }

    @Test
    void findUserById() {
        User user = new User("email@ya.ru", "login", LocalDate.of(1984, 10, 10));
        Integer userId = inMemoryUserStorage.save(user).getId();

        assertTrue(inMemoryUserStorage.findUserById(userId).isPresent());
        assertFalse(inMemoryUserStorage.findUserById(userId + 9999).isPresent());
    }

    @Test
    void findAll() {
        List<User> users = new ArrayList<>(List.of(
                new User("email1@ya.ru", "login1", LocalDate.of(1984, 10, 10)),
                new User("email2@ya.ru", "login2", LocalDate.of(1984, 10, 10)),
                new User("email3@ya.ru", "login3", LocalDate.of(1984, 10, 10))));
        inMemoryUserStorage.save(users.get(0));
        inMemoryUserStorage.save(users.get(1));
        inMemoryUserStorage.save(users.get(2));

        assertEquals(3, inMemoryUserStorage.findAll().size());
    }
}