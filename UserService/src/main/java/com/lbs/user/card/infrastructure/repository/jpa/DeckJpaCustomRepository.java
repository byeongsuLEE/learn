package com.lbs.user.card.infrastructure.repository.jpa;

import com.lbs.user.card.infrastructure.entity.DeckEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-18
 * 풀이방법
 **/

@Repository
@Slf4j
public class DeckJpaCustomRepository {

    @PersistenceContext
    private EntityManager em;


    @Transactional
    public int bulkDescChange(String desc){
        int size = em.createQuery("update DeckEntity d set d.description = :desc ")
                .setParameter("desc", desc)
                .executeUpdate();

        log.info("변경된 개수는 = " + size + "입니다.");
        return  size;
    }

    @Transactional
    public void allCardCountSet(){


       em.createQuery("select DeckEntity d from DeckEntity d").getResultList();
    }


}
