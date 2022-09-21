package com.ch1.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.ch1.domain.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        /* [step 1] UserDao에서 어떤 구현체를 사용할지 UserDao 생성자 파라미터로 넘겨줌
        ConnectionMaker connectionMaker = new DConnectionMaker();
        UserDao dao = new UserDao(connectionMaker);
        */

        /* [step 2] UserDao에서 어떤 ConnectionMaker 구현체를 사용할지 결정하는 기능을 팩토리 클래스로 분리
        UserDao dao = new DaoFactory().userDao();
        */

        // [step 3] 빈 설정 클래스인 DaoFactory를 사용해 ApplicationContext에서 관계설정 및 빈 생성
        // getBean()는 ApplicationContext가 관리하는 오브젝트를 요청하는 메소드
        // -> UserDao() 메소드를 호출해서 그 결과 가져옴.
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);

        User user = new User();
        user.setId("whiteship");
        user.setName("백기선");
        user.setPassword("married");
        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId() + " 조회 성공");
    }
}
