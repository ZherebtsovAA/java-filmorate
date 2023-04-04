package ru.yandex.practicum.filmorate.model;

import lombok.Value;
import java.time.LocalDate;

@Value
public class Film {
    int id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
}