package me.kingcjy.querydslarmtime.user;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import java.sql.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void before() {
        jpaQueryFactory = new JPAQueryFactory(entityManager);
        userRepository.save(new User("KingCjy"));
    }

    @Test
    void test() throws ClassNotFoundException, SQLException {
        User user = userRepository.findById(1L).get();
        UserDto userDto1 = new UserDto(user.getId(), user.getName(), user.getCreatedAt());
        UserDto userDto2 = jpaQueryFactory.select(Projections.constructor(UserDto.class, QUser.user.id, QUser.user.name, QUser.user.createdAt))
                .from(QUser.user)
                .fetchOne();

        System.out.println("userDto1: " + userDto1);
        System.out.println("userDto2: " + userDto2);

        Session session = entityManager.unwrap(Session.class);

        session.doWork(connection ->{
            PreparedStatement preparedStatement = connection.prepareStatement("select * from `user` user0_");
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("timestamp now: " + new Timestamp(System.currentTimeMillis()));
            System.out.println("timestamp now with LocalDateTime: " + Timestamp.valueOf(LocalDateTime.now()));

            if(resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp(2);
                String timestampString = resultSet.getString(2);
                System.out.println("jdbc driver timestamp: " + timestamp);
                System.out.println("jdbc driver timestamp string: " + timestampString);
            }
        });

        assertEquals(userDto1, userDto2);
    }


}