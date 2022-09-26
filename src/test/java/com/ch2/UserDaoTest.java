package com.ch2;

import com.ch3.dao.DaoFactory3;
import com.ch3.dao.UserDao3;
import com.ch3.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.SQLException;

public class UserDaoTest {

    @Test
    void addAndGet() throws SQLException, ClassNotFoundException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory3.class);
        UserDao3 dao = context.getBean("userDao3", UserDao3.class);

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        User user1 = new User("gyumee", "박성철", "springno1");
        User user2 = new User("leegw700", "이길원", "springno2");

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userget1 = dao.get(user1.getId());
        assertThat(userget1.getName(), is(user1.getName()));
        assertThat(userget1.getPassword(), is(user1.getPassword()));
        User userget2 = dao.get(user2.getId());
        assertThat(userget2.getName(), is(user2.getName()));
        assertThat(userget2.getPassword(), is(user2.getPassword()));
    }

    @Test
    void count() throws SQLException, ClassNotFoundException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory3.class);
        UserDao3 dao = context.getBean("userDao3", UserDao3.class);

        User user1 = new User("haneul", "이하늘", "spring1");
        User user2 = new User("sky", "이하늘", "spring2");
        User user3 = new User("ihaneulhi", "이하늘하이", "spring3");

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));
        dao.add(user2);
        assertThat(dao.getCount(), is(2));
        dao.add(user3);
        assertThat(dao.getCount(), is(3));
    }

    @Test
    public void getUserFailure() throws SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory3.class);
        UserDao3 dao = context.getBean("userDao3", UserDao3.class);

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            dao.get("unknown_id");
        });
    }
}
