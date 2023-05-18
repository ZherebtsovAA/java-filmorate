package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @Order(1)
    @Test
    public void contextLoads() {
        assertThat(filmStorage).isNotNull();
    }

    @Order(2)
    @Test
    void save() {
        Film film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1984, 10, 10))
                .duration(100)
                .mpa(Mpa.builder().id(1).build())
                .build();

        assertThat(filmStorage.save(film)).satisfies(value -> {
            assertThat(value.getId()).isEqualTo(1);
            assertThat(value.getName()).isEqualTo("film");
            assertThat(value.getDescription()).isEqualTo("description");
            assertThat(value.getReleaseDate()).isEqualTo(LocalDate.of(1984, 10, 10));
            assertThat(value.getDuration()).isEqualTo(100);
            assertThat(value.getMpa().getId()).isEqualTo(1);
            assertThat(value.getMpa().getName()).isEqualTo("G");
            assertThat(value.getMpa().getDescription()).isEqualTo("у фильма нет возрастных ограничений");
        });
    }

    @Order(3)
    @Test
    void update() {
        Film newFilm = Film.builder()
                .id(1)
                .name("newFilm")
                .description("newDescription")
                .releaseDate(LocalDate.of(2022, 10, 10))
                .duration(60)
                .mpa(Mpa.builder().id(3).build())
                .build();

        assertThat(filmStorage.update(newFilm)).satisfies(value -> {
            assertThat(value.getId()).isEqualTo(1);
            assertThat(value.getName()).isEqualTo("newFilm");
            assertThat(value.getDescription()).isEqualTo("newDescription");
            assertThat(value.getReleaseDate()).isEqualTo(LocalDate.of(2022, 10, 10));
            assertThat(value.getDuration()).isEqualTo(60);
            assertThat(value.getMpa().getId()).isEqualTo(3);
            assertThat(value.getMpa().getName()).isEqualTo("PG-13");
            assertThat(value.getMpa().getDescription()).isEqualTo("детям до 13 лет просмотр не желателен");
        });
    }

    @Order(4)
    @Test
    void findFilmById() {
        assertThat(filmStorage.findFilmById(1)).isPresent().hasValueSatisfying(value -> {
            assertThat(value.getId()).isEqualTo(1);
            assertThat(value.getName()).isEqualTo("newFilm");
            assertThat(value.getDescription()).isEqualTo("newDescription");
            assertThat(value.getReleaseDate()).isEqualTo(LocalDate.of(2022, 10, 10));
            assertThat(value.getDuration()).isEqualTo(60);
            assertThat(value.getMpa().getId()).isEqualTo(3);
            assertThat(value.getMpa().getName()).isEqualTo("PG-13");
            assertThat(value.getMpa().getDescription()).isEqualTo("детям до 13 лет просмотр не желателен");
        });

        assertThat(filmStorage.findFilmById(9999)).isEmpty();
    }

    @Order(5)
    @Test
    void findAll() {
        filmStorage.save(Film.builder()
                .name("film2")
                .description("description2")
                .releaseDate(LocalDate.of(1999, 10, 10))
                .duration(40)
                .mpa(Mpa.builder().id(1).build())
                .build());

        filmStorage.save(Film.builder()
                .name("film3")
                .description("description3")
                .releaseDate(LocalDate.of(2017, 10, 10))
                .duration(70)
                .mpa(Mpa.builder().id(5).build())
                .build());

        assertThat(filmStorage.findAll().size()).isEqualTo(3);
        assertArrayEquals(new Integer[]{1, 2, 3},
                filmStorage.findAll().stream().map(Film::getId).sorted().toArray(Integer[]::new),
                "Arrays should be equal");
    }

    @Order(6)
    @Test
    void addLike() {
        User user = User.builder().id(1).build();
        filmStorage.addLike(filmStorage.findFilmById(1).get(), user);
        filmStorage.addLike(filmStorage.findFilmById(1).get(), user);
        filmStorage.addLike(filmStorage.findFilmById(1).get(), user);

        assertThat(filmStorage.findFilmById(1)).isPresent().hasValueSatisfying(film ->
                assertThat(film).hasFieldOrPropertyWithValue("rate", 3));
    }

    @Order(7)
    @Test
    void deleteLike() {
        User user = User.builder().id(1).build();
        filmStorage.deleteLike(filmStorage.findFilmById(1).get(), user);
        filmStorage.deleteLike(filmStorage.findFilmById(1).get(), user);

        assertThat(filmStorage.findFilmById(1)).isPresent().hasValueSatisfying(film ->
                assertThat(film).hasFieldOrPropertyWithValue("rate", 1));

        filmStorage.deleteLike(filmStorage.findFilmById(1).get(), user);
        filmStorage.deleteLike(filmStorage.findFilmById(1).get(), user);

        assertThat(filmStorage.findFilmById(1)).isPresent().hasValueSatisfying(film ->
                assertThat(film).hasFieldOrPropertyWithValue("rate", 0));
    }
}