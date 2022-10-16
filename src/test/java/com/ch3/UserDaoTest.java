package com.ch3;

import com.ch3.dao.DaoFactory3;
import com.ch3.dao.UserDao3;
import com.ch3.dao.UserDaoJdbc;
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
import java.util.List;

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
    private UserDaoJdbc userDaoJdbc;

    private User user1;
    private User user2;
    private User user3;

    // @BeforeEach 메소드 내 변수값을 테스트 메소드에서 사용하고 싶은 경우 인스턴스 변수를 이용해야 한다.
    @BeforeEach
    public void setUp() {
        this.user1 = new User("gyumee", "name1", "test1");
        this.user2 = new User("leegw700", "name2", "test2");
        this.user3 = new User("bumjin", "name3", "spring");
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        userDaoJdbc.deleteAll();
        assertThat(userDaoJdbc.getCount(), is(0));

        userDaoJdbc.add(user1);
        userDaoJdbc.add(user2);
        assertThat(dao.getCount(), is(2));

        User userget1 = userDaoJdbc.get(user1.getId());
        assertThat(userget1.getName(), is(user1.getName()));
        assertThat(userget1.getPassword(), is(user1.getPassword()));

        User userget2 = userDaoJdbc.get(user2.getId());
        assertThat(userget2.getName(), is(user2.getName()));
        assertThat(userget2.getPassword(), is(user2.getPassword()));
    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {
        userDaoJdbc.deleteAll();
        assertThat(userDaoJdbc.getCount(), is(0));

        userDaoJdbc.add(user1);
        assertThat(userDaoJdbc.getCount(), is(1));

        userDaoJdbc.add(user2);
        assertThat(userDaoJdbc.getCount(), is(2));

        userDaoJdbc.add(user3);
        assertThat(userDaoJdbc.getCount(), is(3));
    }

    @Test
    void getUserFailure() throws SQLException {
        userDaoJdbc.deleteAll();
        assertThat(userDaoJdbc.getCount(), is(0));
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            userDaoJdbc.get("unknown_id");
        });
    }

    @Test
    void getAll() {
        userDaoJdbc.deleteAll();

        // query() 메소드에서 조회 결과가 없는 경우 크기 0인 리스트를 반환하는지 테스트
        List<User> users0 = userDaoJdbc.getAll();
        assertThat(users0.size(), is(0));

        userDaoJdbc.add(user1);  // Id: gyumee
        List<User> users1 = userDaoJdbc.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));

        userDaoJdbc.add(user2);  // Id: leegw700
        List<User> users2 = userDaoJdbc.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        // getAll(): id순으로 정렬해 반환
        // 따라서 알파벳순으로 가장 빠른 user3가 gitAll() 반환값의 첫번째에 위치
        userDaoJdbc.add(user3);  // Id: bumjin
        List<User> users3 = userDaoJdbc.getAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user3, users3.get(0));
        checkSameUser(user1, users3.get(1));
        checkSameUser(user2, users3.get(2));
    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
    }
}
