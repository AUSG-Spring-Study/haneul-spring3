package com.ch4;

import com.ch4.dao.DaoFactory4;
import com.ch4.dao.UserDao;
import com.ch4.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoFactory4.class)
// 애플리케이션 컨텍스트의 설정파일 위치 지정
// 스프링은 여러 테스트 클래스에서 같은 설정파일을 사용해도 단 한 개의 애플리케이션 컨텍스트만 만들어 공유한다. -> 성능 향상
public class UserDaoTest {
    // @Autowired는 스프링의 컨텍스트 내에서 정의된 빈 중에서 인스턴스 변수에 주입 가능한 타입의 빈을 찾아준다.
    // UserDao는 UserDaoJdbc가 구현한 인터페이스이므로 UserDao에 UserDaoJdbc 빈 주입 가능
    // 구현체가 바뀌어도 빈 주입 코드는 바꿀 필요가 없으므로 스프링 빈을 인터페이스로 가져오도록 하는 것이 좋다.
    @Autowired
    private UserDao userDao;
    @Autowired
    private DataSource dataSource;

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
        userDao.deleteAll();
        assertThat(userDao.getCount(), is(0));

        userDao.add(user1);
        userDao.add(user2);
        assertThat(userDao.getCount(), is(2));

        User userget1 = userDao.get(user1.getId());
        assertThat(userget1.getName(), is(user1.getName()));
        assertThat(userget1.getPassword(), is(user1.getPassword()));

        User userget2 = userDao.get(user2.getId());
        assertThat(userget2.getName(), is(user2.getName()));
        assertThat(userget2.getPassword(), is(user2.getPassword()));
    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {
        userDao.deleteAll();
        assertThat(userDao.getCount(), is(0));

        userDao.add(user1);
        assertThat(userDao.getCount(), is(1));

        userDao.add(user2);
        assertThat(userDao.getCount(), is(2));

        userDao.add(user3);
        assertThat(userDao.getCount(), is(3));
    }

    @Test
    void getUserFailure() throws SQLException {
        userDao.deleteAll();
        assertThat(userDao.getCount(), is(0));
        assertThrows(EmptyResultDataAccessException.class, () -> {
            userDao.get("unknown_id");
        });
    }

    @Test
    void getAll() {
        userDao.deleteAll();

        // query() 메소드에서 조회 결과가 없는 경우 크기 0인 리스트를 반환하는지 테스트
        List<User> users0 = userDao.getAll();
        assertThat(users0.size(), is(0));

        userDao.add(user1);  // Id: gyumee
        List<User> users1 = userDao.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));

        userDao.add(user2);  // Id: leegw700
        List<User> users2 = userDao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        // getAll(): id순으로 정렬해 반환
        // 따라서 알파벳순으로 가장 빠른 user3가 gitAll() 반환값의 첫번째에 위치
        userDao.add(user3);  // Id: bumjin
        List<User> users3 = userDao.getAll();
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

    // DataAccessException을 잡아서 처리하려면 미리 학습 테스트를 만들어서 실제로 전환되는 예외의 종류를 확인해야 한다.
    @Test
    void duplicateKey() {
        userDao.deleteAll();

        userDao.add(user1);

        // DuplicateKeyException
        assertThrows(DataAccessException.class, () -> {
            userDao.add(user1); // 같은 사용자를 두 번 등록하면 예외가 발생할 것이다.
        });
    }

    /*
        1. 강제로 DuplicateKeyException을 발생시킨다.
        2. 위 예외는 중첩된 예외로 SQLException을 내부에 갖고 있다. getRootCause() 메소드로 중첩된 예외를 가져온다.
        3. 주입받은 dataSource를 이용해 SQLERrorCodeSQLExceptionTranslator의 오브젝트를 만든다.
        4. translate() 메소드를 호출해 SQLException을 DataAccessException 타입의 예외로 변환해준다.
        5. 변환된 DataAccessException 타입의 예외가 정확히 DuplicateKeyException 타입인지 확인
     */
    @Test
    public void sqlExceptionTranslate() {
        userDao.deleteAll();

        try {
            userDao.add(user1);
            userDao.add(user2);
        }
        catch(DuplicateKeyException ex) {
            SQLException sqlEx = (SQLException)ex.getRootCause();
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);

            assertThat(set.translate(null, null, sqlEx), is(DuplicateKeyException.class));
        }
    }

}
