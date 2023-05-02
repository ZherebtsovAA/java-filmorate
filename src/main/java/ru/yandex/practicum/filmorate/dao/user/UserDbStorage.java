package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.CustomerFriendDb;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final CustomerFriendDb customerFriendDb;

    public boolean isNotExists(int userId) {
        String sqlQuery = "SELECT count(*) FROM customer WHERE customer_id = ?";
        //noinspection ConstantConditions: return value is always an int, so NPE is impossible here
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, userId);

        return result != 1;
    }

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
    public User update(User user) {
        Integer userId = user.getId();
        if (userId == null || userId < 1 || isNotExists(userId)) {
            throw new NotFoundException("пользователь с id{" + userId + "} не обновлен, нет в списке пользователей");
        }

        String sqlQuery = "UPDATE customer SET " +
                "login = ?, name = ?, email = ?, birthday = ? WHERE customer_id = ?";

        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                userId);

        return user;
    }

    @Override
    public Optional<User> findUserById(Integer userId) {
        if (userId == null || userId < 1 || isNotExists(userId)) {
            return Optional.empty();
        }

        User user = jdbcTemplate.queryForObject(
                "SELECT customer_id, login, name, email, birthday FROM customer WHERE customer_id = ?",
                (resultSet, rowNum) -> User.builder()
                        .id(resultSet.getInt("CUSTOMER_ID"))
                        .login(resultSet.getString("LOGIN"))
                        .name(resultSet.getString("NAME"))
                        .email(resultSet.getString("EMAIL"))
                        .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                        .build(), userId);

        //noinspection ConstantConditions: наличие user проверено методом isNotExists(userId)
        return Optional.of(user);
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM customer ORDER BY customer_id", userRowMapper);
    }

    private final RowMapper<User> userRowMapper = (resultSet, rowNum) -> User.builder()
            .id(resultSet.getInt("CUSTOMER_ID"))
            .login(resultSet.getString("LOGIN"))
            .name(resultSet.getString("NAME"))
            .email(resultSet.getString("EMAIL"))
            .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
            .build();

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
        Set<Integer> friendsId = customerFriendDb.findFriendsId(userId);
        List<User> friends = new ArrayList<>();
        for (Integer id : friendsId) {
            findUserById(id).ifPresent(friends::add);
        }

        return friends;
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

        List<User> commonFriends = new ArrayList<>(commonFriendsId.size());
        for (Integer id : commonFriendsId) {
            findUserById(id).ifPresent(commonFriends::add);
        }

        return commonFriends;
    }

}