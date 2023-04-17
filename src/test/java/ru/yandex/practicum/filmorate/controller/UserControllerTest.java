package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void userCreate() throws Exception {
        User user = new User("mail@mail.ru", "dolore", LocalDate.of(1946, 8, 20));
        user.setId(1);
        user.setName("Nick Name");
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void userCreateFailLogin() throws Exception {
        User user = new User("yandex@mail.ru", "dolore ullamco", LocalDate.of(2446, 8, 20));
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    public void userUpdate() throws Exception {
        User userUpdate = new User("mail@yandex.ru", "doloreUpdate",
                LocalDate.of(1976, 9, 20));
        userUpdate.setId(1);
        userUpdate.setName("est adipisicing");
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(userUpdate))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void userUpdateUnknown() throws Exception {
        User userUpdate = new User("mail@yandex.ru", "doloreUpdate",
                LocalDate.of(1976, 9, 20));
        userUpdate.setId(9999);
        userUpdate.setName("est adipisicing");
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(userUpdate))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void userGetAll() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isOk());
    }
}