package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CustomerFriendDb {
    private final JdbcTemplate jdbcTemplate;

    public List<User> findFriends(Integer userId) {
        String sqlQuery = "SELECT * FROM customer WHERE customer_id IN " +
                "(SELECT friend_id FROM customer_friend WHERE USER_ID = ? AND STATUS = ?)";
        return jdbcTemplate.query(sqlQuery, userRowMapper, userId, true);
    }

    public Set<Integer> findFriendsId(Integer userId) {
        String sqlQuery = "SELECT friend_id " +
                "FROM customer_friend " +
                "WHERE user_id = ? AND status = ? " +
                "ORDER BY friend_id";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, friendIdRowMapper, userId, true));
    }

    public void addToFriends(User user, User friend) {
        int userId = user.getId();
        int friendId = friend.getId();

        //noinspection ConstantConditions: return value is always an int, so NPE is impossible here
        int result = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM customer_friend WHERE user_id = ? AND friend_id = ? AND status = ?",
                Integer.class, userId, friendId, true);

        if (result == 0) { //пришел первичный запрос на добавление в друзья
            jdbcTemplate.update(
                    "INSERT INTO customer_friend (user_id, friend_id, status) values (?, ?, ?)",
                    userId, friendId, true); //подтвержденную связь
        }
    }

    public void deleteToFriends(User user, User friend) {
        String sqlQuery = "DELETE FROM customer_friend WHERE user_id = ? AND friend_id = ? AND status = ?";
        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId(), true);
    }

    private final RowMapper<User> userRowMapper = (resultSet, rowNum) -> User.builder()
            .id(resultSet.getInt("CUSTOMER_ID"))
            .login(resultSet.getString("LOGIN"))
            .name(resultSet.getString("NAME"))
            .email(resultSet.getString("EMAIL"))
            .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
            .build();

    private final RowMapper<Integer> friendIdRowMapper = (resultSet, rowNum) -> resultSet.getInt("FRIEND_ID");

}