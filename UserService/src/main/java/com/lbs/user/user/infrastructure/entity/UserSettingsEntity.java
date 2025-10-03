package com.lbs.user.user.infrastructure.entity;

import com.lbs.user.user.domain.UserSettings;
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

    public static UserSettingsEntity create(UserSettings userSettings, UserEntity user) {
        return UserSettingsEntity.builder()
                .studyNotify(userSettings.studyNotify())
                .friendActivityNotify(userSettings.friendActivityNotify())
                .achieveNotify(userSettings.achieveNotify())
                .emailNotify(userSettings.emailNotify())
                .language(userSettings.language())
                .theme(userSettings.theme())
                .studyGoal(userSettings.studyGoal())
                .autoPlay(userSettings.autoPlay())
                .profileVisible(userSettings.profileVisible())
                .studyStatsVisible(userSettings.studyStatsVisible())
                .friendsCanMessage(userSettings.studyNotify())
                .user(user)
                .build();
    }


    public UserSettings mapToDomain(Long userId, UserSettingsEntity entity){
        // user 지연로딩으로 인한 추가적인 쿼리를 방지하기 하기 위해 userId를 매개변수로 받음
        return new UserSettings(
                entity.id,
                userId,
                entity.studyNotify,
                entity.friendActivityNotify,
                entity.achieveNotify,
                entity.emailNotify,
                entity.language,
                entity.theme,
                entity.studyGoal,
                entity.autoPlay,
                entity.profileVisible,
                entity.studyStatsVisible,
                entity.friendsCanMessage
        );
    }

    public void updateUserSettings(UserSettings userSettings) {
        this.studyNotify = userSettings.studyNotify();
        this.friendActivityNotify = userSettings.friendActivityNotify();
        this.achieveNotify = userSettings.achieveNotify();
        this.emailNotify = userSettings.emailNotify();
        this.language = userSettings.language();
        this.theme = userSettings.theme();
        this.studyGoal = userSettings.studyGoal();
        this.autoPlay = userSettings.autoPlay();
        this.profileVisible = userSettings.profileVisible();
        this.studyStatsVisible = userSettings.studyStatsVisible();
        this.friendsCanMessage = userSettings.friendsCanMessage();
    }
}
