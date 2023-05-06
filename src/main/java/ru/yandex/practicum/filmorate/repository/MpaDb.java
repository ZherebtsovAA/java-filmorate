package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MpaDb {
    private final JdbcTemplate jdbcTemplate;

    public Mpa findMpaById(Integer mpaId) throws NotFoundException {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT mpa_id, name, description FROM mpa_rating WHERE mpa_id = ?",
                    mpaRowMapper, mpaId);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("записи о возрастном ограничении с id{" + mpaId + "} не найдено");
        }
    }

    public List<Mpa> findAll() {
        return jdbcTemplate.query("SELECT * FROM mpa_rating", mpaRowMapper);
    }

    public Map<Integer, Mpa> findMpaByListId(List<Integer> mpaIds) {
        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        SqlParameterSource parameters = new MapSqlParameterSource("ids", mpaIds);
        List<Mpa> mpaList = namedJdbcTemplate.query(
                "SELECT * FROM mpa_rating WHERE mpa_id IN (:ids) ORDER BY mpa_id",
                parameters, mpaRowMapper);

        return mpaList.stream().
                collect(Collectors.toMap(Mpa::getId, Function.identity()));
    }

    private final RowMapper<Mpa> mpaRowMapper = (resultSet, rowNum) -> Mpa.builder()
            .id(resultSet.getInt("MPA_ID"))
            .name(resultSet.getString("NAME"))
            .description(resultSet.getString("DESCRIPTION"))
            .build();

}