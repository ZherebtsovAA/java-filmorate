package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Order(1)
    @Test
    void contextLoads() {
        assertThat(userStorage).isNotNull();
    }

    @Order(2)
    @Test
    void save() {
        User user = User.builder()
                .login("login")
                .name("user")
                .email("user@ya.ru")
                .birthday(LocalDate.of(1984, 10, 10))
                .build();

        assertThat(userStorage.save(user)).satisfies(value -> {
            assertThat(value.getId()).isEqualTo(1);
            assertThat(value.getLogin()).isEqualTo("login");
            assertThat(value.getName()).isEqualTo("user");
            assertThat(value.getEmail()).isEqualTo("user@ya.ru");
            assertThat(value.getBirthday()).isEqualTo(LocalDate.of(1984, 10, 10));
        });
    }

    @Order(3)
    @Test
    void update() {
        User userUpdate = User.builder()
                .id(1)
                .login("loginUpdate")
                .name("userUpdate")
                .email("user@ya.ruUpdate")
                .birthday(LocalDate.of(1986, 5, 12))
                .build();

        assertThat(userStorage.update(userUpdate)).satisfies(value -> {
            assertThat(value.getId()).isEqualTo(1);
            assertThat(value.getLogin()).isEqualTo("loginUpdate");
            assertThat(value.getName()).isEqualTo("userUpdate");
            assertThat(value.getEmail()).isEqualTo("user@ya.ruUpdate");
            assertThat(value.getBirthday()).isEqualTo(LocalDate.of(1986, 5, 12));
        });

        userUpdate.setId(9999);

        assertThatThrownBy(() -> userStorage.update(userUpdate)).isInstanceOf(NotFoundException.class);
    }

    @Order(4)
    @Test
    void findUserById() {
        assertThat(userStorage.findUserById(1)).isPresent().hasValueSatisfying(value -> {
            assertThat(value.getId()).isEqualTo(1);
            assertThat(value.getLogin()).isEqualTo("loginUpdate");
            assertThat(value.getName()).isEqualTo("userUpdate");
            assertThat(value.getEmail()).isEqualTo("user@ya.ruUpdate");
            assertThat(value.getBirthday()).isEqualTo(LocalDate.of(1986, 5, 12));
        });

        assertThat(userStorage.findUserById(9999)).isEmpty();
    }

    @Order(5)
    @Test
    void findAll() {
        userStorage.save(User.builder()
                .login("login2")
                .name("user2")
                .email("user2@ya.ru")
                .birthday(LocalDate.of(1981, 10, 10))
                .build());

        userStorage.save(User.builder()
                .login("login3")
                .name("user3")
                .email("user3@ya.ru")
                .birthday(LocalDate.of(1983, 10, 10))
                .build());

        assertThat(userStorage.findAll().size()).isEqualTo(3);
        assertArrayEquals(new Integer[]{1, 2, 3},
                userStorage.findAll().stream().map(User::getId).sorted().toArray(Integer[]::new),
                "Arrays should be equal");
    }

    @Order(6)
    @Test
    void addToFriends() {
        User user = User.builder().id(1).build();
        User friend = User.builder().id(2).build();

        assertThat(userStorage.findUserById(1).get().getFriends()).isNull();

        userStorage.addToFriends(user, friend);
        friend.setId(3);
        userStorage.addToFriends(user, friend);

        assertThat(userStorage.findUserFriends(1).size()).isEqualTo(2);
    }

    @Order(7)
    @Test
    void deleteToFriends() {
        User user = User.builder().id(1).build();
        User friend = User.builder().id(2).build();

        assertThat(userStorage.findUserFriends(1).size()).isEqualTo(2);
        userStorage.deleteToFriends(user, friend);

        assertThat(userStorage.findUserFriends(1).size()).isEqualTo(1);
        assertThat(userStorage.findUserFriends(1).get(0)).hasFieldOrPropertyWithValue("id", 3);
    }

    @Order(8)
    @Test
    void findCommonFriends() {
        userStorage.save(User.builder()
                .login("login4")
                .name("user4")
                .email("user4@ya.ru")
                .birthday(LocalDate.of(1983, 10, 10))
                .build());

        userStorage.save(User.builder()
                .login("login5")
                .name("user5")
                .email("user5@ya.ru")
                .birthday(LocalDate.of(1983, 10, 10))
                .build());

        User user = User.builder().id(1).build();
        User otherUser = User.builder().id(5).build();
        userStorage.addToFriends(user, otherUser);
        otherUser.setId(4);
        userStorage.addToFriends(user, otherUser);
        otherUser.setId(3);
        userStorage.addToFriends(user, otherUser);
        otherUser.setId(2);
        userStorage.addToFriends(user, otherUser);

        assertThat(userStorage.findUserFriends(1).size()).isEqualTo(4);

        otherUser.setId(5);
        userStorage.addToFriends(otherUser, user);
        user.setId(2);
        userStorage.addToFriends(otherUser, user);
        user.setId(3);
        userStorage.addToFriends(otherUser, user);
        user.setId(4);
        userStorage.addToFriends(otherUser, user);

        assertThat(userStorage.findUserFriends(5).size()).isEqualTo(4);

        List<Integer> friendsId = userStorage.findCommonFriends(1, 5).stream()
                .map(User::getId)
                .sorted()
                .collect(Collectors.toList());

        assertThat(List.of(2, 3, 4).size() == friendsId.size()
                && List.of(2, 3, 4).containsAll(friendsId) && friendsId.containsAll(List.of(2, 3, 4))).isTrue();
    }

}