package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.ValidReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    @NotBlank
    private final String name;
    @NotBlank
    @Size(max = 200)
    private final String description;
    @ValidReleaseDate
    private final LocalDate releaseDate;
    @PositiveOrZero
    private final int duration;
    private Set<Integer> rating = new HashSet<>();

    public void setRating(Integer id) {
        rating.add(id);
    }
}