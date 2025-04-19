package com.lbs.user.common.mapper;

import com.lbs.user.card.domain.AuditInfo;
import com.lbs.user.user.infrastructure.entity.BaseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-11
 * 풀이방법
 **/


@Mapper(componentModel = "spring")
public interface AuditInfoMapper {
        static AuditInfo entityToAuditInfoStatic(BaseEntity baseEntity) {
        return AuditInfo.builder()
                .createdDate(baseEntity.getCreatedDate())
                .createdBy(baseEntity.getCreatedBy())
                .lastModifiedDate(baseEntity.getLastModifiedDate())
                .lastModifiedBy(baseEntity.getLastModifiedBy())
                .build();
    }

    @Named("entityToAuditInfo")
    AuditInfo entityToAuditInfo(BaseEntity entity);

    // 아래의 코드는 안된다. static이 아니기때문에 다른 mapper클래스에서 사용하지 못함.
    // AuditInfo entityToAuditInfo(BaseEntity entity);
//    static AuditInfo entityToAuditInfo(BaseEntity deckEntity) {
//        return AuditInfo.builder()
//                .createdDate(deckEntity.getCreatedDate())
//                .createdBy(deckEntity.getCreatedBy())
//                .lastModifiedDate(deckEntity.getLastModifiedDate())
//                .lastModifiedBy(deckEntity.getLastModifiedBy())
//                .build();
//    }
}
