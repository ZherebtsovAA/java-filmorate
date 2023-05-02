package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDb {
    private final JdbcTemplate jdbcTemplate;

    public boolean isNotExists(int mpaId) {
        String sqlQuery = "SELECT count(*) FROM mpa_rating WHERE mpa_id = ?";
        //noinspection ConstantConditions: return value is always an int, so NPE is impossible here
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, mpaId);

        return result != 1;
    }

    public Mpa findMpaById(Integer mpaId) throws NotFoundException {
        if (isNotExists(mpaId)) {
            throw new NotFoundException("записи о возрастном ограничении с id{" + mpaId + "} не найдено");
        }

        return jdbcTemplate.queryForObject(
                "SELECT mpa_id, name, description FROM mpa_rating WHERE mpa_id = ?",
                (resultSet, rowNum) -> Mpa.builder()
                        .id(resultSet.getInt("MPA_ID"))
                        .name(resultSet.getString("NAME"))
                        .description(resultSet.getString("DESCRIPTION"))
                        .build(), mpaId);
    }

    public List<Mpa> findAll() {
        return jdbcTemplate.query("SELECT * FROM mpa_rating", mpaRowMapper);
    }

    private final RowMapper<Mpa> mpaRowMapper = (resultSet, rowNum) -> Mpa.builder()
            .id(resultSet.getInt("MPA_ID"))
            .name(resultSet.getString("NAME"))
            .description(resultSet.getString("DESCRIPTION"))
            .build();

}