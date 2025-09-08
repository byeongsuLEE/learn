package com.lbs.user.video.infrastructure.repository;

import com.lbs.user.video.infrastructure.entity.VideoEntity;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class VideoJPARepositoryImplTest {

    private static final Logger log = LoggerFactory.getLogger(VideoJPARepositoryImplTest.class);
    @Autowired
    private VideoJPARepository videoJPARepository;

    @Test
    void saveVideo() {

        VideoEntity videoEntity = VideoEntity.builder()
                .title("title")
                .description("description")
                .tag("만화")
                .videoURL("https://storage.googleapis.com/evil55/videos/%ED%8C%A8%ED%8A%B8%EC%99%80%EB%A7%A4%ED%8A%B8%203%ED%99%94-%EA%B3%BC%EC%9E%90%EB%A7%8C%EB%93%A4%EA%B8%B0%23Pat%20and%20Mat%20EP-3.mp4")
                .userId(1L)
                .build();

        VideoEntity basicEntity = videoJPARepository.save(videoEntity);
        VideoEntity savedVideoEntity = videoJPARepository.findById(basicEntity.getId())
                .orElseGet(() -> null);


        log.info("저장된 날짜 {}  {} {} {} " ,basicEntity.getCreatedBy(),basicEntity.getCreatedBy(),basicEntity.getLastModifiedBy(),basicEntity.getLastModifiedBy());
        assertThat(basicEntity.getId()).isEqualTo(savedVideoEntity.getId());
        assertThat(basicEntity).isEqualTo(savedVideoEntity);




    }

    @Test
    void deleteVideo() {
    }

    @Test
    void getVideo() {
    }

    @Test
    void getAllVideos() {
    }
}