package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.CustomerFriendDb;

import java.util.*;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final CustomerFriendDb customerFriendDb;

    @Override
    public User save(User user) {
        SimpleJdbcInsert insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("CUSTOMER")
                .usingGeneratedKeyColumns("CUSTOMER_ID");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("LOGIN", user.getLogin())
                .addValue("NAME", user.getName())
                .addValue("EMAIL", user.getEmail())
                .addValue("BIRTHDAY", user.getBirthday());

        Number userId = insertUser.executeAndReturnKey(parameters);
        user.setId(userId.intValue());

        return user;
    }

    @Override
    public User update(User user) throws NotFoundException {
        Integer userId = user.getId();
        if (userId == null || userId < 1) {
            throw new NotFoundException("пользователь с id{" + userId + "} не обновлен, нет в списке пользователей");
        }

        String sqlQuery = "UPDATE customer SET " +
                "login = ?, name = ?, email = ?, birthday = ? WHERE customer_id = ?";

        int result = jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                userId);

        if (result == 0) {
            throw new NotFoundException("пользователь с id{" + userId + "} не обновлен, нет в списке пользователей");
        }

        return user;
    }

    @Override
    public Optional<User> findUserById(Integer userId) {
        if (userId == null || userId < 1) {
            return Optional.empty();
        }

        try {
            User user = jdbcTemplate.queryForObject(
                    "SELECT customer_id, login, name, email, birthday FROM customer WHERE customer_id = ?",
                    userRowMapper, userId);

            //noinspection ConstantConditions
            return Optional.of(user);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM customer ORDER BY customer_id", userRowMapper);
    }

    @Override
    public void addToFriends(User user, User friend) {
        customerFriendDb.addToFriends(user, friend);
    }

    @Override
    public void deleteToFriends(User user, User friend) {
        customerFriendDb.deleteToFriends(user, friend);
    }

    @Override
    public List<User> findUserFriends(Integer userId) {
        return customerFriendDb.findFriends(userId);
    }

    @Override
    public List<User> findCommonFriends(Integer userId, Integer otherId) {
        Set<Integer> userFriendsId = customerFriendDb.findFriendsId(userId);
        if (userFriendsId == null || userFriendsId.size() == 0) {
            return Collections.emptyList();
        }

        Set<Integer> commonFriendsId = new HashSet<>(userFriendsId);
        Set<Integer> otherFriendsId = customerFriendDb.findFriendsId(otherId);
        if (otherFriendsId == null || otherFriendsId.size() == 0) {
            return Collections.emptyList();
        }
        commonFriendsId.retainAll(otherFriendsId);

        return getUserListById(commonFriendsId);
    }

    public List<User> getUserListById(Set<Integer> userIds) {
        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        SqlParameterSource parameters = new MapSqlParameterSource("ids", userIds);
        return namedJdbcTemplate.query(
                "SELECT * FROM customer WHERE customer_id IN (:ids)",
                parameters, userRowMapper);
    }

    private final RowMapper<User> userRowMapper = (resultSet, rowNum) -> User.builder()
            .id(resultSet.getInt("CUSTOMER_ID"))
            .login(resultSet.getString("LOGIN"))
            .name(resultSet.getString("NAME"))
            .email(resultSet.getString("EMAIL"))
            .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
            .build();

}