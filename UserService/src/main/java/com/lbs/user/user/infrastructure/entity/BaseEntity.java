package com.lbs.user.user.infrastructure.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-05
 * base entity
 *  추상 클래스로 만들어서 baseEntity 테이블은 생성하지않는다.
 *  공통 속성을 추가하기 위해서 사용한다.
 **/



@MappedSuperclass
@Getter
public abstract class BaseEntity {
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
}
