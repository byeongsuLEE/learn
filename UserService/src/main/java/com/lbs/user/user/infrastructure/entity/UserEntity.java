package com.lbs.user.user.infrastructure.entity;

import com.lbs.user.card.infrastructure.entity.CardEntity;
import com.lbs.user.user.domain.Address;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-05
 * 풀이방법
 **/

@Table(name="users")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
@AllArgsConstructor
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id ;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private LocalDateTime joinDate;

    @Transient
    private String one;

    @Embedded
    Address address;

    // OAuth2 관련 필드 추가
    @Column
    private String name;
    @Column
    private String imageUrl;
    @Column
    private String provider;
    @Column
    private String providerId;

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();



    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserSettingsEntity userSettings;

}
