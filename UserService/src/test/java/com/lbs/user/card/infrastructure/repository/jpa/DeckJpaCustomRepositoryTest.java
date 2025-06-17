package com.lbs.user.card.infrastructure.repository.jpa;

import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.dto.response.DeckResponseDto;
import com.lbs.user.card.infrastructure.entity.DeckEntity;
import com.lbs.user.card.infrastructure.repository.DeckRepository;
import com.lbs.user.card.mapper.DeckMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class DeckJpaCustomRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(DeckJpaCustomRepositoryTest.class);

    @MockitoBean
    DeckJpaCustomRepository deckJpaCustomRepository;

    @MockitoBean
    JpaDeckRepository jpaDeckRepository;


    @MockitoBean  // 이렇게 Mock으로 처리
    DeckRepository deckRepository;

    @PersistenceContext
    EntityManager em;


    @MockitoBean
    DeckMapper deckMapper;


    @Test
    public void update_AllDeckCardCount(){
        deckRepository.setDeckCardCount();
        assertTrue(true);
    }


//    @Test
//    public void 순수_bulkUpdate(){
//        int cnt = deckJpaCustomRepository.bulkDescChange("bulk 순수 JPA 확인");
//        // !!! 벌크 사용시 주의할점
//        em.flush();
//        em.clear();
//        Assertions.assertThat(cnt).isEqualTo(5);
//    }
//
//    @Test
//    @Rollback(false)
//    public void JPA_bulkUpdate(){
//        jpaDeckRepository.save(DeckEntity.createDeck("bulk 사용 시 주의할점","bulk는 영속성컨텍스트에 들어가지 않는다.","bulk","bulk"));
//        int cnt = jpaDeckRepository.bulkUpdateDescription("bulk JPA TEST 확인");
//
//        // !!! 벌크 사용시 주의할점
//        em.flush();
//        em.clear();
//
//        List<DeckEntity> all = jpaDeckRepository.findAll();
//        DeckEntity deckEntity = all.get(all.size() - 1);
//        log.info("deckentity description= "+ deckEntity.getDescription());
//
//        Assertions.assertThat(cnt).isEqualTo(5);
//    }

    @Test
    @Rollback(false)
    public void JAP_EntityGraph(){
        List<DeckEntity> all = jpaDeckRepository.findAll();
        List<DeckResponseDto> list = all.stream().map(deckMapper::entityToResponseDto).toList();

        for ( DeckResponseDto dto : list ) {
            log.info(dto.toString()+"\n");
        }
        assertTrue(true);

    }

    @Test
    @Transactional
    public void readLockjpa(){
        List<DeckEntity> all = jpaDeckRepository.findAllBy();
        List<DeckResponseDto> list = all.stream().map(deckMapper::entityToResponseDto).toList();

        for ( DeckResponseDto dto : list ) {
            log.info(dto.toString()+"\n");
        }
        assertTrue(true);
    }

    @Test
    @Transactional
    public void updateDeck(){
        List<DeckEntity> all = jpaDeckRepository.findAllBy();
        List<DeckResponseDto> list = all.stream().map(deckMapper::entityToResponseDto).toList();

        for ( DeckResponseDto dto : list ) {
            log.info(dto.toString()+"\n");
        }
        assertTrue(true);
    }






}