package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
@NonNull
public class User {
    private final int id;
    private final String email;
    private final String login;
    private final String name;
    private final LocalDate birthday;
}
