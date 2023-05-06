package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.repository.MpaDb;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDb mpaDb;

    public Mpa findMpaById(Integer mpaId) throws IncorrectParameterException {
        if (mpaId == null || mpaId < 1) {
            throw new IncorrectParameterException("id{" + mpaId + "}", "передано некорректное значение");
        }
        return mpaDb.findMpaById(mpaId);
    }

    public List<Mpa> findAll() {
        return mpaDb.findAll();
    }

}