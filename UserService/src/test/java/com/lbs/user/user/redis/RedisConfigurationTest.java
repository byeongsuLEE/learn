package com.lbs.user.user.redis;

import com.lbs.user.card.infrastructure.entity.DeckEntity;
import com.lbs.user.card.infrastructure.repository.jpa.JpaDeckRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisConfigurationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    JpaDeckRepository jpaDeckRepository;
    private static final Logger log = LoggerFactory.getLogger(RedisConfigurationTest.class);
    private final String TEST_KEY = "test:connection";


//    @BeforeEach
//    @Transactional
//    @Rollback(false)
//    void setUp() {
//        DeckEntity deckEntity = jpaDeckRepository.findById(5L).orElseGet(null);
//        deckEntity.updateCardCount(0);
//
//        System.out.println("-----------------------setup-----------");
//        // 테스트 시작 전에 기존 테스트 키가 있다면 삭제
//        redisTemplate.delete(TEST_KEY);
//    }


    @Test
    @Transactional
    @Rollback(false)
    void 레디스_연결_테스트(){
        String ping = redisTemplate.getConnectionFactory().getConnection().ping();

        // 성공하면 pong이 와야함
        System.out.println(ping);

        Assertions.assertThat(ping).isEqualTo("PONG");

    }

    /**
     * 레디스 트랜잭션 exec 전 값 확인 테스트
     * @작성자   : lbs
     * @작성일   : 2025-05-18
     * @설명     : 레디스의 트랜잭션은 exec가 되어야 값이 적용된다.
     * storeRedisValue : 예상값은 null로 될것이다.
     */
    @Test
    @Transactional
    void 레디스_데이터_저장(){

        DeckEntity deckEntity = jpaDeckRepository.findById(5L).orElseGet(null);
        deckEntity.updateCardCount(3);

        redisTemplate.opsForValue().set(TEST_KEY, 1234);

        Integer storeRedisValue = (Integer)redisTemplate.opsForValue().get(TEST_KEY);

        Assertions.assertThat(storeRedisValue).isNull();

    }

    @Test
    @Rollback(false) // 테스트 자체는 롤백하지 않음 (성공 시)
    void 레디스_데이터_저장_에러발생시켜보기() {
        // 수동으로 트랜잭션 관리
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        try {
            // 트랜잭션 내에서 실행
            transactionTemplate.execute(status -> {
                DeckEntity deckEntity = jpaDeckRepository.findById(5L).orElseThrow();
                int originalCount = deckEntity.getCardCount();
                System.out.println("원래 카드 수: " + originalCount);

                // 데이터 변경
                deckEntity.updateCardCount(4444);
                redisTemplate.opsForValue().set(TEST_KEY, 4444);

                System.out.println("변경된 카드 수: " + deckEntity.getCardCount());
                System.out.println("Redis 값 설정: " + 4444);

                // 의도적으로 예외 발생 (이 경우 트랜잭션이 자동으로 롤백됨)
                throw new RuntimeException("의도적인 예외 발생");
            });
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        // 트랜잭션 외부에서 값 확인 (롤백 여부 확인)
        DeckEntity afterRollback = jpaDeckRepository.findById(5L).orElseThrow();
        System.out.println("최종 카드 수: " + afterRollback.getCardCount());

        Object redisValue = redisTemplate.opsForValue().get(TEST_KEY);
        System.out.println("최종 Redis 값: " + redisValue);
    }


    @Test
    @Transactional
    void 레디스_트랜잭션_롤백_테스트() {
        // 1. 초기 상태 기록
        DeckEntity deckEntity = jpaDeckRepository.findById(5L).orElseThrow();
        int originalCount = deckEntity.getCardCount();
        System.out.println("원래 카드 수: " + originalCount);

        // 2. 데이터 변경
        deckEntity.updateCardCount(7777);
        redisTemplate.opsForValue().set(TEST_KEY, 7777);

        System.out.println("변경 중 카드 수: " + deckEntity.getCardCount());
        System.out.println("변경 중 Redis 값: " + redisTemplate.opsForValue().get(TEST_KEY));

        // 3. 상태 확인을 위한 대기
        try {
            System.out.println("5초 대기 - 변경 중 DB/Redis 상태 확인...");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 4. 예외 발생
        throw new RuntimeException("의도적인 예외 발생 - 롤백 유도");

        // 이 지점 이후의 코드는 실행되지 않음
    }



    @Test
    void 레디스_분산_트랜잭션_적용(){

    }


}