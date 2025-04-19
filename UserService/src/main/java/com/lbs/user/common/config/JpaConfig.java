package com.lbs.user.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-09
 * Jpa Auditing 사용하기 위한 설정 클래스
 **/

@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
