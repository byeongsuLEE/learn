package com.lbs.user;

import com.lbs.user.user.domain.Address;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;

@SpringBootTest
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @KafkaListener(topics = "test-topic", groupId = "user")
    public void testListener(String message) {
        System.out.println("Consumed: " + message);
    }

    @PersistenceContext
    private EntityManager em;

    @Test
    @Transactional
    void testFindUser() {
//        UserEntity user = em.find(UserEntity.class, 3L);
        em.createQuery("select u from UserEntity u where u.id = 3").getResultList();
//        em.persist(user);
        em.flush();
        em.clear();


//        if (user != null) {
//            System.out.println("email = " + user.getEmail());
//        } else {
//            System.out.println("User not found");
//        }

    }

    @Test
    @Transactional
    void 테스트_JPQL(){

        // jpql
        List<Address> user = em.createQuery("select u.address from UserEntity u where u.id =:id")
                .setParameter("id", 3l)
                .getResultList();

        System.out.println("✅ 실행 완료: 유저 정보 = "+ user.get(0).getCity());
    }

}
