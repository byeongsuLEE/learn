package com.lbs.user.user.infrastructure.repository;

import com.lbs.user.card.infrastructure.entity.DeckEntity;
import com.lbs.user.user.infrastructure.entity.UserEntity;
import com.lbs.user.user.domain.Address;
import com.lbs.user.video.dto.response.VideoResponseDto;
import com.lbs.user.video.infrastructure.entity.VideoEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    private static final Logger log = LoggerFactory.getLogger(JPATest.class.getName());
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


    @Test
    @Transactional
    void 패치성능테스트() {
        em.createQuery("select deck from DeckEntity deck join fetch deck.cards", DeckEntity.class)
                .getResultList();
    }


        @Test
    @Transactional
    void dto조회성능테스트() {
        measurePerformance("dto 변환",
                ()->{
                    List<VideoEntity> videoEntities = em.createQuery("select v from VideoEntity v",VideoEntity.class).getResultList();
                    return videoEntities.stream()
                            .map(VideoResponseDto::fromEntity)
                            .collect(Collectors.toList());
                });

        em.flush();
        em.clear();




        measurePerformance("dto 직접 쿼리 조회 " ,
                ()-> em.createQuery("select new com.lbs.user.video.dto.response.VideoQueryDto(v.id," +
                        "v.title," +
                        "v.description," +
                        "v.tag," +
                        "v.videoURL," +
                        "v.thumbnailURL," +
                        "v.userId," +
                        "v.createdBy," +
                        "v.createdDate," +
                        "v.lastModifiedBy," +
                        "v.lastModifiedDate) from VideoEntity v " ));
        assertThat(true).isTrue();
    }

    public <T> T measurePerformance (String taskName , Supplier<T> task){
        log.info(taskName + " 실행 시작");
        long startTime = System.currentTimeMillis();
        T result = task.get();
        long endTime = System.currentTimeMillis();
        long duration  = endTime - startTime;
        log.info(taskName + " 실행 시간 : "  + duration+ " ms)" );
        return result;
    }
}
