package com.lbs.user.card.domain;

import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-11
 * @AllArgsConstructor를 하는 이유
 * - entity 생성된 후 결과값을 반화하기 위해서 auditinfo가 필요하고 수정되지 않기 때문에 썼다.
 **/


@Getter
@AllArgsConstructor
@ToString
@Builder
public class AuditInfo {
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

//    @Builder
//    private AuditInfo(String createdBy, LocalDateTime createdDate, String lastModifiedBy, LocalDateTime lastModifiedDate) {
//        this.createdBy = createdBy;
//        this.createdDate = createdDate;
//        this.lastModifiedBy = lastModifiedBy;
//        this.lastModifiedDate = lastModifiedDate;
//    }
//
//    public AuditInfo createAudit(String createdBy, LocalDateTime createdDate, String lastModifiedBy, LocalDateTime lastModifiedDate) {
//        return new AuditInfo(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
//    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditInfo auditInfo = (AuditInfo) o;
        return Objects.equals(getCreatedBy(), auditInfo.getCreatedBy()) && Objects.equals(getCreatedDate(), auditInfo.getCreatedDate()) && Objects.equals(getLastModifiedBy(), auditInfo.getLastModifiedBy()) && Objects.equals(getLastModifiedDate(), auditInfo.getLastModifiedDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCreatedBy(), getCreatedDate(), getLastModifiedBy(), getLastModifiedDate());
    }
}
