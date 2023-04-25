package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(inMemoryUserStorage);
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    private final FilmService filmService = new FilmService(inMemoryFilmStorage, userService);

    @Test
    void findFilmById() {
        Film film = new Film("film", "desc", LocalDate.of(2023, 4, 22), 180);
        Integer filmId = filmService.save(film).getId();

        assertEquals(filmId, filmService.findFilmById(filmId).getId());
        assertThrows(NotFoundException.class, () -> filmService.findFilmById(filmId + 9999));
    }

    @Test
    void addLike() {
        List<User> users = new ArrayList<>(List.of(
                new User("email1@ya.ru", "login1", LocalDate.of(1984, 10, 10)),
                new User("email2@ya.ru", "login2", LocalDate.of(1984, 10, 10)),
                new User("email3@ya.ru", "login3", LocalDate.of(1984, 10, 10))));
        userService.save(users.get(0));
        userService.save(users.get(1));
        userService.save(users.get(2));
        Film film = new Film("film", "desc", LocalDate.of(2023, 4, 22), 180);
        Integer filmId = filmService.save(film).getId();
        filmService.addLike(filmId, users.get(0).getId());
        filmService.addLike(filmId, users.get(1).getId());
        filmService.addLike(filmId, users.get(2).getId());

        assertEquals(3, filmService.findFilmById(filmId).getRating().size());

        filmService.addLike(filmId, users.get(0).getId());

        assertEquals(3, filmService.findFilmById(filmId).getRating().size());
    }

    @Test
    void deleteLike() {
        List<User> users = new ArrayList<>(List.of(
                new User("email1@ya.ru", "login1", LocalDate.of(1984, 10, 10)),
                new User("email2@ya.ru", "login2", LocalDate.of(1984, 10, 10)),
                new User("email3@ya.ru", "login3", LocalDate.of(1984, 10, 10))));
        userService.save(users.get(0));
        userService.save(users.get(1));
        userService.save(users.get(2));
        Film film = new Film("film", "desc", LocalDate.of(2023, 4, 22), 180);
        Integer filmId = filmService.save(film).getId();
        filmService.addLike(filmId, users.get(0).getId());
        filmService.addLike(filmId, users.get(1).getId());
        filmService.addLike(filmId, users.get(2).getId());

        assertEquals(3, filmService.findFilmById(filmId).getRating().size());

        filmService.deleteLike(filmId, users.get(0).getId());
        filmService.deleteLike(filmId, users.get(1).getId());
        filmService.deleteLike(filmId, users.get(2).getId());

        assertEquals(0, filmService.findFilmById(filmId).getRating().size());

        filmService.deleteLike(filmId, users.get(1).getId());

        assertEquals(0, filmService.findFilmById(filmId).getRating().size());
    }

    @Test
    void findPopularFilms() {
        userService.save(new User("email1@ya.ru", "login1", LocalDate.of(1984, 10, 10)));
        userService.save(new User("email2@ya.ru", "login2", LocalDate.of(1984, 10, 10)));
        userService.save(new User("email3@ya.ru", "login3", LocalDate.of(1984, 10, 10)));
        filmService.save(new Film("film1", "desc1", LocalDate.of(2023, 4, 22), 180));
        filmService.save(new Film("film2", "desc2", LocalDate.of(2023, 4, 22), 180));
        filmService.save(new Film("film3", "desc3", LocalDate.of(2023, 4, 22), 180));

        filmService.addLike(1, 1);
        filmService.addLike(2, 1);
        filmService.addLike(2, 2);
        filmService.addLike(3, 1);
        filmService.addLike(3, 2);
        filmService.addLike(3, 3);

        assertEquals(3, filmService.findPopularFilms(10).size(), "Метод вернул отличное от реального значения количество фильмов");
        assertTrue(filmService.findPopularFilms(-10).isEmpty(), "Возвращен не пустой список с фильмами при переденном отрицательном количестве");
        assertEquals(3, filmService.findPopularFilms(10).get(0).getId(), "Не верно формируется список ТОП фильмов");
    }
}