package me.kingcjy.querydslarmtime.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private JPAQueryFactory jpaQueryFactory;

    @BeforeEach
    void before() {
        jpaQueryFactory = new JPAQueryFactory(entityManager);

        userRepository.save(new User("KingCjy"));
    }

    @Test
    void test() {
        User user = userRepository.findById(1L).get();
        User user2 = jpaQueryFactory.select(QUser.user).from(QUser.user).fetchOne();

        System.out.println(user);
        System.out.println(user2);

        assertSame(user, user2);
    }
}