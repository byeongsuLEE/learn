package com.lbs.user.video.infrastructure.repository;

import com.lbs.user.video.infrastructure.entity.QVideoEntity;
import com.lbs.user.video.infrastructure.entity.VideoEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.lbs.user.video.infrastructure.entity.QVideoEntity.videoEntity;

/**
 * 작성자  : lbs
 * 날짜    : 2025-09-04
 * 풀이방법
 **/


@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    private static final Logger log = LoggerFactory.getLogger(QuerydslBasicTest.class);
    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;


    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Test
    public void startQuerydsl() {
        VideoEntity videoEntity = VideoEntity.builder().title("test")
                .url("test")
                .description("test")
                .tag("영상")
                .userId(1L)
                .build();
        em.persist(videoEntity);

        JPAQueryFactory query = new JPAQueryFactory(em);

        QVideoEntity qVideoEntity = QVideoEntity.videoEntity;

        VideoEntity result = query.selectFrom(qVideoEntity)
                .where(qVideoEntity.id.eq(videoEntity.getId()))
                .fetchOne();

        Assertions.assertThat(videoEntity).isEqualTo(result);
    }

    @Test
    public void Fetch_테스트() {


//     `   //List
//        List<VideoEntity> videoEntityList = queryFactory
//                .selectFrom(QVideoEntity.videoEntity)
//                .fetch();
//        for (VideoEntity videoEntity : videoEntityList) {
//            System.out.println(videoEntity.toString());
//        }
//
//
//        //단건
//        VideoEntity videoEntity = queryFactory
//                .selectFrom(QVideoEntity.videoEntity)
//                .fetchFirst();
//        log.info(videoEntity.toString());

        // 페이징
        QueryResults<VideoEntity> videoEntityQueryResults = queryFactory
                .selectFrom(videoEntity)
                .fetchResults();


        Assertions.assertThat(true).isTrue();

    }

    @Test
    public void 정렬() {
        List<VideoEntity> fetch = queryFactory.selectFrom(videoEntity)
                .orderBy(videoEntity.id.desc())
                .fetch();
        for (VideoEntity videoEntity : fetch) {
            log.info(videoEntity.toString());
        }
        Assertions.assertThat(true).isTrue();
    }

    @Test
    public void 페이징() {

        List<VideoEntity> fetch = queryFactory.selectFrom(videoEntity)
                .orderBy(videoEntity.id.desc())
                .offset(2)
                .limit(11)
                .fetch();

        for (VideoEntity videoEntity : fetch) {
            log.info(videoEntity.toString());
        }
        Assertions.assertThat(true).isTrue();

    }

    @Test
    public void 동적쿼리영상검색() {
        String keyword = null;

        //1. booleanBuilder 방법
        BooleanBuilder builder = new BooleanBuilder();

//        builder.or(videoEntity.title.containsIgnoreCase(keyword)); // 대소문자 구분하기 위해서  contains 대신 사용
//        builder.or(videoEntity.description.containsIgnoreCase(keyword));
//        builder.or(videoEntity.tag.containsIgnoreCase(keyword));
//
//        List<VideoEntity> fetch = queryFactory.selectFrom(videoEntity)
//                .where(builder)
//                .fetch();

        //2. 다중 파라미터 방법 ( 실무 추천)
        Pageable pageable = PageRequest.of(0, 10);

        List<VideoEntity> fetch;
        fetch = queryFactory.selectFrom(videoEntity)
                .where(
                        searchContains(keyword)
                )
                .orderBy(videoEntity.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        for (VideoEntity videoEntity : fetch) {
            log.info(videoEntity.toString());
        }

        Assertions.assertThat(true).isTrue();
        //나중에 유저 이름도 추가합시다.


    }
//     주석된 이유 - 메서드 체인에서 null 값이 반환될 경우 nullpointexception 에러가 발생한다.
//    private BooleanExpression tagContains(String keyword) {
//        return  keyword!=null ?  videoEntity.tag.containsIgnoreCase(keyword) : null ;
//    }
//
//    private BooleanExpression descriptionContains(String keyword) {
//        return  keyword!=null ? videoEntity.description.containsIgnoreCase(keyword) : null;
//    }
//
//    private BooleanExpression titleContains(String keyword) {
//        return keyword!=null ?  videoEntity.title.containsIgnoreCase(keyword) : null ;
//    }

    private BooleanExpression searchContains(String keyword) {
        // 키워드가 없으면 null 반환 -> where 절에서 무시됨
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }

        // OR 조건을 직접 연결하여 하나의 BooleanExpression으로 반환
        return videoEntity.title.containsIgnoreCase(keyword)
                .or(videoEntity.description.containsIgnoreCase(keyword))
                .or(videoEntity.tag.containsIgnoreCase(keyword));
    }
}
