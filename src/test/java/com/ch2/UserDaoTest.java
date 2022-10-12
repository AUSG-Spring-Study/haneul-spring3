package com.ch2;

import com.ch3.dao.DaoFactory3;
import com.ch3.dao.UserDao3;
import com.ch3.dao.UserDaoJdbcTemplate;
import com.ch3.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoFactory3.class)
// 애플리케이션 컨텍스트의 설정파일 위치 지정
// 스프링은 여러 테스트 클래스에서 같은 설정파일을 사용해도 단 한 개의 애플리케이션 컨텍스트만 만들어 공유한다. -> 성능 향상
public class UserDaoTest {
    @Autowired
    ApplicationContext context;
    @Autowired
    private UserDao3 dao;
    @Autowired
    private UserDaoJdbcTemplate userDaoJdbcTemplate;

    private User user1;
    private User user2;
    private User user3;

    // @BeforeEach 메소드 내 변수값을 테스트 메소드에서 사용하고 싶은 경우 인스턴스 변수를 이용해야 한다.
    @BeforeEach
    public void setUp() {
        this.user1 = new User("user1", "name1", "test1");
        this.user2 = new User("user2", "name2", "test2");
        this.user3 = new User("user3", "name3", "spring");
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        userDaoJdbcTemplate.deleteAll();
        assertThat(userDaoJdbcTemplate.getCount(), is(0));

        userDaoJdbcTemplate.add(user1);
        userDaoJdbcTemplate.add(user2);
        assertThat(dao.getCount(), is(2));

        User userget1 = userDaoJdbcTemplate.get(user1.getId());
        assertThat(userget1.getName(), is(user1.getName()));
        assertThat(userget1.getPassword(), is(user1.getPassword()));

        User userget2 = userDaoJdbcTemplate.get(user2.getId());
        assertThat(userget2.getName(), is(user2.getName()));
        assertThat(userget2.getPassword(), is(user2.getPassword()));
    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {
        userDaoJdbcTemplate.deleteAll();
        assertThat(userDaoJdbcTemplate.getCount(), is(0));

        userDaoJdbcTemplate.add(user1);
        assertThat(userDaoJdbcTemplate.getCount(), is(1));

        userDaoJdbcTemplate.add(user2);
        assertThat(userDaoJdbcTemplate.getCount(), is(2));

        userDaoJdbcTemplate.add(user3);
        assertThat(userDaoJdbcTemplate.getCount(), is(3));
    }

    @Test
    void getUserFailure() throws SQLException {
        userDaoJdbcTemplate.deleteAll();
        assertThat(userDaoJdbcTemplate.getCount(), is(0));
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            userDaoJdbcTemplate.get("unknown_id");
        });
    }
}
