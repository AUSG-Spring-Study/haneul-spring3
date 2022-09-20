package ch1.dao;

import ch1.domain.User;

import java.sql.*;

public class UserDao {
    private ConnectionMaker connectionMaker;

    /*
    // 인터페이스를 사용해 DB 커넥션을 제공하는 클래스에 대한 구체적인 정보(DB 커넥션 url)는 UserDao에서 제거했지만,
    // 어떤 클래스의 오브젝트를 사용할지 결정하는(D사, N사, ..) UserDao 생성자 코드는 남아 있다.
    // 여전히 확장을 위해선 UserDao 클래스의 코드 수정이 필요하다.
    public UserDao() {
        connectionMaker = new DConnectionMaker();
    }
     */

    // 클라이언트: main() 메소드
    // 클라이언트로부터 ConnectionMaker 오브젝트를 전달받음
    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeConnection();
        PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());
        ps.executeUpdate();
        ps.close();
        c.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeConnection();
        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        rs.next();

        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        rs.close();
        ps.close();
        c.close();

        return user;
    }
}
