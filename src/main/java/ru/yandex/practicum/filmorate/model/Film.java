package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotBlank
    private final String name;
    @NotBlank
    @Size(max=200)
    private final String description;
    @Past
    @NotNull
    private final LocalDate releaseDate;
    @PositiveOrZero
    private final int duration;
}