package com.ch3.dao;

import com.ch3.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao3 {
    private DataSource dataSource;
    private JdbcContext jdbcContext;

    public void setDataSource(DataSource dataSource) {
        this.jdbcContext = new JdbcContext();
        this.jdbcContext.setDataSource(dataSource);
        this.dataSource = dataSource;
    }

    public UserDao3(DataSource dataSource) {
        setDataSource(dataSource);
    }

    // 로컬 클래스: 클래스 안에 내부 클래스로 구현한 것
    // --> 클래스 파일을 줄일 수 있고, 자신이 선언된 곳의 정보에 접근할 수 있다.
    // add의 파라미터 user 변수에 final을 붙임으로써 내부 클래스(AddStatement)의 코드에서 외부의 메소드(add()) 로컬 변수(user)에 직접 접근할 수 있다.
    public void add(final User user) throws ClassNotFoundException, SQLException {
        this.jdbcContext.workWithStatementStrategy(
                new StatementStrategy() {
                    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                        PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
                        ps.setString(1, user.getId());
                        ps.setString(2, user.getName());
                        ps.setString(3, user.getPassword());
                        return ps;
                    }
                }
        );
    }

    // SQL 쿼리를 실행하고 ResultSet을 통해 가져온 결과를 매핑해 User 오브젝트 생성하는 메소드
    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = this.dataSource.getConnection();
        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();

        User user = null;
        if(rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if (user == null) throw new EmptyResultDataAccessException(1);

        return user;
    }

    // 익명 클래스:
    public void deleteAll() throws SQLException {
        this.jdbcContext.workWithStatementStrategy (
            new StatementStrategy() {
                // 템플릿으로부터 Connection을 제공받아서 PreparedStatement를 만들어 돌려주는 메소드
                public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                    return c.prepareStatement("delete from users");
                }
            }
        );
    }

    // SQL 쿼리를 실행하고 ResultSet을 통해 결과 값을 가져오는 메소드
    public int getCount() throws SQLException {
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("select count(*) from users");

        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt(1);

        rs.close();
        ps.close();
        c.close();

        return count;
    }
}
