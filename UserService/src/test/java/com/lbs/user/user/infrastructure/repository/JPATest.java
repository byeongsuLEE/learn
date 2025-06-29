package com.lbs.user.user.infrastructure.repository;

import com.lbs.user.card.infrastructure.entity.DeckEntity;
import com.lbs.user.user.infrastructure.entity.UserEntity;
import com.lbs.user.user.domain.Address;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-06
 * 풀이방법
 **/

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //실제 db 사용
public class JPATest {
    @PersistenceContext
    private EntityManager em;

//    @Test
//    @Transactional
//    void update_엔티티업데이트실험(){
//
//        DeckEntity deckEntity = em.find(DeckEntity.class, 1L);
//        deckEntity.setCategory("dsfsdfds");
//
//        em.flush();
//        em.clear();
//    }

    @Test
    @Transactional
    void entityManager_동작확인() {
        UserEntity result = em.createQuery("SELECT u FROM UserEntity u WHERE u.id = :id", UserEntity.class)
                .setParameter("id", 1L)
                .getSingleResult();

        em.flush();
        em.clear();
        //임베디드 타입 가져오기
        Address result2 = em.createQuery("SELECT u.address FROM UserEntity u WHERE u.id = :id", Address.class)
                .setParameter("id", 3L)
                .getSingleResult();
        System.out.println("✅ 유저 이메일: " + result2.getCity());
        assertThat(result2).isNotNull();

    }

}
