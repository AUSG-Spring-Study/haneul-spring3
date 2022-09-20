package ch1.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정 클래스임을 명시하는 어노테이션
@Configuration
public class DaoFactory {
    // 오브젝트 생성을 담당하는 IoC용 메소드임을 명시하는 어노테이션
    // 해당 메소드 이름이 빈의 이름이 된다.
    @Bean
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }
}
