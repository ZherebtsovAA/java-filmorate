package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();

    @Test
    void save() {
        Film film = new Film("newFilm", "newDesc", LocalDate.of(2023, 4, 22), 180);

        assertNull(film.getId());
        assertEquals(1, inMemoryFilmStorage.save(film).getId());
    }

    @Test
    void update() {
        Film film = new Film("newFilm", "newDesc", LocalDate.of(2023, 4, 22), 180);
        Integer filmId = inMemoryFilmStorage.save(film).getId();
        Film filmUpdate = new Film("filmUpdate", "descUpdate", LocalDate.of(2023, 4, 22), 180);
        filmUpdate.setId(filmId);
        inMemoryFilmStorage.update(filmUpdate);

        assertNotEquals(film.getName(), filmUpdate.getName());

        film.setId(filmId + 9999);
        assertThrows(NotFoundException.class, () -> inMemoryFilmStorage.update(film));
    }

    @Test
    void findFilmById() {
        Film film = new Film("newFilm", "newDesc", LocalDate.of(2023, 4, 22), 180);
        Integer filmId = inMemoryFilmStorage.save(film).getId();

        assertTrue(inMemoryFilmStorage.findFilmById(filmId).isPresent());
        assertFalse(inMemoryFilmStorage.findFilmById(filmId + 9999).isPresent());
    }

    @Test
    void findAll() {
        List<Film> films = new ArrayList<>(List.of(
                new Film("film1", "desc1", LocalDate.of(2023, 4, 22), 180),
                new Film("film2", "desc2", LocalDate.of(2023, 4, 22), 180),
                new Film("film3", "desc3", LocalDate.of(2023, 4, 22), 180)));
        inMemoryFilmStorage.save(films.get(0));
        inMemoryFilmStorage.save(films.get(1));
        inMemoryFilmStorage.save(films.get(2));

        assertEquals(3, inMemoryFilmStorage.findAll().size());
    }
}