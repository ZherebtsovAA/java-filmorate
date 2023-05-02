package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CustomerFriendDb {
    private final JdbcTemplate jdbcTemplate;

    public Set<Integer> findFriendsId(Integer userId) {
        String sqlQuery = "SELECT friend_id " +
                "FROM customer_friend " +
                "WHERE user_id = ? AND status = ? " +
                "ORDER BY friend_id";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, friendIdRowMapper, userId, true));
    }

    private final RowMapper<Integer> friendIdRowMapper = (resultSet, rowNum) -> resultSet.getInt("FRIEND_ID");

    public void addToFriends(User user, User friend) {
        int userId = user.getId();
        int friendId = friend.getId();
        //noinspection ConstantConditions: return value is always an int, so NPE is impossible here
        int result = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM customer_friend WHERE user_id = ? AND friend_id = ? AND status = ?",
                Integer.class, userId, friendId, true);

        //noinspection ConstantConditions: return value is always an int, so NPE is impossible here
        result = result + jdbcTemplate.queryForObject(
                "SELECT count(*) FROM customer_friend WHERE user_id = ? AND friend_id = ? AND status = ?",
                Integer.class,friendId, userId, false);

        if (result == 0) { //пришел первичный запрос на добавление в друзья
            jdbcTemplate.update(
                    "INSERT INTO customer_friend (user_id, friend_id, status) values (?, ?, ?)",
                    userId, friendId, true); //подтвержденную связь

            jdbcTemplate.update(
                    "INSERT INTO customer_friend (user_id, friend_id, status) values (?, ?, ?)",
                    friendId, userId, false); //неподтвержденную связь
            return;
        }

        //noinspection ConstantConditions: return value is always an int, so NPE is impossible here
        result = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM customer_friend WHERE user_id = ? AND friend_id = ? AND status = ?",
                Integer.class,friendId, userId, false);

        if (result == 1) { //пришел запрос на подтверждение связи
            jdbcTemplate.update(
                    "UPDATE customer_friend SET status = ? WHERE user_id = ? AND friend_id = ? AND status = ?",
                    true, friendId, userId, false);
        }
    }

    public void deleteToFriends(User user, User friend) {
        String sqlQuery = "UPDATE customer_friend SET status = ? WHERE user_id = ? AND friend_id = ? AND status = ?";
        jdbcTemplate.update(sqlQuery, false, user.getId(), friend.getId(), true);
    }

}