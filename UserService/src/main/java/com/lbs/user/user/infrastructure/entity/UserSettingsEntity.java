package com.lbs.user.user.infrastructure.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-01
 * 풀이방법
 **/

@Table(name = "user_settings")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    //알림 설정
    @Column(nullable = false , columnDefinition = "boolean default false")
    private boolean studyNotify;

    @Column(nullable = false , columnDefinition = "boolean default false")
    private boolean friendActivityNotify;

    @Column(nullable = false , columnDefinition = "boolean default false")
    private boolean achieveNotify;

    @Column(nullable = false , columnDefinition = "boolean default false")
    private boolean emailNotify;


    //학습 설정
    @Column( nullable = false, length = 20, columnDefinition = "varchar(20) default '한국어'")
    private String language = "한국어";

    @Column(nullable = false , length = 20 , columnDefinition = "varchar(20) default '라이트'")
    private String theme = "라이트";

    @Column
    private Integer studyGoal;

    @Column(nullable = false , columnDefinition = "boolean default false")
    private boolean autoPlay;


    // 개인정보 설정
    @Column(nullable = false , columnDefinition = "boolean default false")
    private boolean profileVisible;

    @Column(nullable= false, columnDefinition = "boolean default false")
    private boolean studyStatsVisible;

    @Column(nullable= false, columnDefinition = "boolean default false")
    private boolean friendsCanMessage;


    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;






}
