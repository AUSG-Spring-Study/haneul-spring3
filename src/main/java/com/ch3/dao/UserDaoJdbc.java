package com.ch3.dao;

import com.ch3.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc {
    // 스프링에서 제공하는 템플릿 콜백
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public UserDaoJdbc(DataSource dataSource) {
        setDataSource(dataSource);
    }

    // RowMapper 콜백 오브젝트에는 상태정보가 없다.
    // 따라서 하나의 콜백 오브젝트를 멀티스레드에서 동시에 사용해도 문제가 되지 않는다.
    // RowMapper 콜백은 하나만 만들어 공유하자.
    private RowMapper<User> userMapper =
            new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    return user;
                }
            };

    // 로컬 클래스: 클래스 안에 내부 클래스로 구현한 것
    // --> 클래스 파일을 줄일 수 있고, 자신이 선언된 곳의 정보에 접근할 수 있다.
    // add의 파라미터 user 변수에 final을 붙임으로써 내부 클래스(AddStatement)의 코드에서 외부의 메소드(add()) 로컬 변수(user)에 직접 접근할 수 있다.
    public void add(final User user) {
        this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)",
             user.getId(), user.getName(), user.getPassword());
    }

    /*
     get(): SQL 쿼리를 실행하고 ResultSet을 통해 가져온 결과를 매핑해 User 오브젝트 생성하는 메소드
     getCount()에 적용했던 ResultSetExtractor 콜백 대신 RowMapper 콜백 사용
     ResultSetExtractor와 RowMapper 모두 템플릿으로부터 ResultSet을 전달받고, 필요한 정보를 추출해 리턴한다.
     다른 점은 ResultSetExtractor는 ResultSet을 한 번 전달받아 추출 작업을 진행하고 최종 결과를 리턴하지만,
     RowMapper는 ResultSet의 row 하나를 매핑하기 위해 사용되기 때문에 여러 번 호출 가능하다

     1. queryForObject()에서 SQL 실행
     2. 실행 결과인 ResultSet의 next() 실행해서 첫 번째 로우로 이동시킨 후 RowMapper 콜백 호출
     -> RowMapper가 호출되는 시점에 이미 ResultSet은 첫 번째 로우를 가리키므로 rs.next() 호출 필요 없음
     3. RowMapper는 ResultSet이 가리키고 있는 로우의 내용을 User 오브젝트에 그대로 담아 get() 메소드에 리턴
     4. queryForObject()는 조회 결과가 없는 경우 EmptyResultDataAccessException을 던진다.
    */
    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[] {id},  // SQL에 바인딩할 파라미터 값, 가변인자 대신 배열을 사용한다.
                this.userMapper
        );
    }

    // 익명 클래스:
    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

/*
    / query() 메소드 사용해 getCount() 구현
    // query()는 PreparedStatementCreator 콜백과 ResultSetExtractor 콜백을 파라미터로 받는 템플릿
    // PreparedStatementCreator는 템플릿으로부터 Connection을 받고 PreparedStatement를 돌려주는 콜백
    // ResultSetExtractor는 PreparedStatement의 쿼리를 실행해서 얻은 ResultSet을 전달받는 콜백

    public int getCount() throws SQLException {
        return this.jdbcTemplate.query(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                return con.prepareStatement("select count(*) from users");
            }
        }, new ResultSetExtractor<Integer>() {
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                return rs.getInt(1);
            }
        });
    }
*/

    // queryFoObject() 사용해 getCount() 구현
    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

    // query()는 조회 결과가 없는 경우 크기가 0인 List<T> 오브젝트를 반환한다.
    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id",
            this.userMapper);
    }
}
