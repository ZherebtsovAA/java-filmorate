package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class Film {
    int id;
    @NotNull
    String name;
    @NotBlank
    String description;
    LocalDate releaseDate;
    int duration;
}