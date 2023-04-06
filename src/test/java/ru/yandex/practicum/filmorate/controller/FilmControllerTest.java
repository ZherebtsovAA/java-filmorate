package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void filmCreate() throws Exception {
        Film film = new Film("nisi eiusmod", "adipisicing",
                LocalDate.of(1967, 3, 25), 100);
        film.setId(1);
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    @Test
    public void filmCreateFailName() throws Exception {
        Film film = new Film("", "Description",
                LocalDate.of(1900, 3, 25), 200);
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    public void filmUpdate() throws Exception {
        Film filmUpdate = new Film("Film Updated", "New film update decription",
                LocalDate.of(1989, 4, 17), 190);
        filmUpdate.setId(1);
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(filmUpdate))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void filmUpdateUnknown() throws Exception {
        Film filmUpdate = new Film("Film Updated", "New film update decription",
                LocalDate.of(1989, 4, 17), 190);
        filmUpdate.setId(9999);
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(filmUpdate))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void filmGetAll() throws Exception {
        mockMvc.perform(get("/films")).andExpect(status().isOk());
    }
}