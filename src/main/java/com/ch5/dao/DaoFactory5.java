package com.ch5.dao;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

// 애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정 클래스임을 명시하는 어노테이션
@Configuration
public class DaoFactory5 {
    // 오브젝트 생성을 담당하는 IoC용 메소드임을 명시하는 어노테이션
    // 해당 메소드 이름이 빈의 이름이 된다.
    @Bean
    public DataSource connectionMaker() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost:53306/springbook");
        dataSource.setUsername("spring");
        dataSource.setPassword("book");
        return dataSource;
    }

    // 빈의 이름은 클래스의 구현 인터페이스 이름을 따르는 것이 좋다.
    // -> 그래야 나중에 구현 클래스를 바꿔도 혼란이 없기 때문
    @Bean
    public UserDaoJdbc userDao() { return new UserDaoJdbc(connectionMaker()); }
}
