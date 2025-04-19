package com.lbs.user.common.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-09
 * 풀이방법
 **/

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // 나중에 	SecurityContextHolder.getContext()로 인증 정보 꺼내줘야한다.
        return Optional.of("INITAL");
    }
}
