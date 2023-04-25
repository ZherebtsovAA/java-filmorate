package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Integer id;
    @Email
    private final String email;
    @NotBlank
    @Pattern(regexp = "\\S+", message = "логин не может содержать пробелы")
    private final String login;
    private String name;
    @Past
    private final LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();

    public void setFriends(Integer id) {
        friends.add(id);
    }
}