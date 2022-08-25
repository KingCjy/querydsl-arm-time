package me.kingcjy.querydslarmtime.user;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
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

        System.out.println(userDto1);
        System.out.println(userDto2);

        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection ->{
            PreparedStatement preparedStatement = connection.prepareStatement("select * from \"user\" user0_");
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp(2);
                System.out.println("jdbc driver timestamp: " + timestamp);
            }
        });

        assertEquals(userDto1, userDto2);
    }


}