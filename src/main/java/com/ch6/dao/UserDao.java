package com.ch6.dao;


import com.ch6.domain.User;

import java.util.List;

// 데이터 엑세스 기술(JPA, Hibernate, JDO ..)에 따른 독립적인 UerDao를 만들기 위해 인터페이스와 구현 분리
public interface UserDao {
    /*
        데이터 엑세스 기술마다 던지는 예외가 다르므로 예외에 따라 메소드의 선언이 달라진다.
        다행히도 JDO, Hibernate, JPA는 SQLException같은 체크 예외 대신 런타임 예외를 사용하기 때문에 메소드에 throws 선언을 해주지 않아도 된다.
        JDBC에서 SQLException을 런타임 예외로 포장해 던진다면 예외에 따라 메소드 선언을 다르게 할 필요없이 아래와 같이 선언할 수 있게 된다.
    */
    void add(User user);
    User get(String id);
    List<User> getAll();
    void deleteAll();
    int getCount();
    void update(User user);
}
