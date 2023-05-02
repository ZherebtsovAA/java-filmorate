package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDb;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDb genreDb;

    public Genre findGenreById(Integer genreId) throws IncorrectParameterException {
        if (genreId == null || genreId < 1) {
            throw new IncorrectParameterException("id{" + genreId + "}", "передано некорректное значение");
        }
        return genreDb.findGenreById(genreId);
    }

    public List<Genre> findAll() {
        return genreDb.findAll();
    }

}