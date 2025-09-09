package com.lbs.user.video.infrastructure.repository;

import com.lbs.user.video.infrastructure.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoJPARepository extends JpaRepository<VideoEntity,Long> {

    List<VideoEntity> findAllBy();

}
