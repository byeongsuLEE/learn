package com.lbs.user.user.jwt;

import com.lbs.user.common.response.ApiResponse;
import com.lbs.user.common.response.ErrorCode;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * jwtAuthentication 에서 발생한 에러 처리 필터
 * 작성자  : lbs
 * 날짜    : 2025-05-11
 * 풀이방법
 **/

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTExceptionFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            handleJwtException(request, response, e);
        }
    }

    /**
     * jwt에서 발생할 수 있는 모든 예외 처리 핸들러
     * @작성자   : lbs
     * @작성일   : 2025-05-11
     * @설명     : 리프레쉬 토큰 유무, db 리프레쉬/헤더 리프레쉬 비교
     * @param request
     * @param response
     * @param e
     * @throws IOException
     */
    private void handleJwtException(HttpServletRequest request, HttpServletResponse response, JwtException e) throws IOException {
        ErrorCode errorCode = findErrorCodeByMessage(e.getMessage());

        if (isMakeAccessToken(request, errorCode)) {
            try {
                Authentication authentication = getAuthentication(request);
                String newAccessToken = createNewAccessToken(request, response,authentication);
                updateSecurityContext(request, newAccessToken, response, authentication);
                sendSuccessResponse(response);
                return;
            }catch (Exception ex){
                log.error("토큰 갱신 중 오류 발생: {}", ex.getMessage());
                // 토큰 갱신 실패 시 만료된 리프레시 토큰 오류로 응답
                sendErrorResponse(response, ErrorCode.EXPIRED_REFRESH_TOKEN);
                return;
            }
        }
        sendErrorResponse(response, errorCode);
    }

    private Authentication getAuthentication(HttpServletRequest request) {
        String refreshToken = request.getHeader("refreshToken");
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        // 저장된 리프레시 토큰 확인 (Redis 활용 시)
        // if (redisUtil.getData(authentication.getName()) == null) {
        //     throw new JwtException(ErrorCode.EXPIRED_REFRESH_TOKEN.getMessage());
        // }

        return authentication;
    }

    /**
     * security에 인증객체 넣기
     * @작성자   : lbs
     * @작성일   : 2025-05-11
     * @설명     : jwt 활용해서 securityContextHolder에 인증객체 넣기
     * @param request
     * @param newAccessToken
     * @param response
     * @param authentication
     */
    private void updateSecurityContext(HttpServletRequest request, String newAccessToken, HttpServletResponse response, Authentication authentication) {
        //응답 헤더에 토큰 설정
        response.setHeader("Authorization", newAccessToken);
        response.setHeader("refreshToken", request.getHeader("refreshToken"));
        response.setStatus(HttpStatus.CREATED.value());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendSuccessResponse(HttpServletResponse response) throws IOException {
        setResponse(response, HttpStatus.CREATED);
        ApiResponse<String> apiResponse = ApiResponse.success(HttpStatus.CREATED, "토큰 재발급 성공");
        response.getWriter().write(apiResponse.toJson());
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        setResponse(response, errorCode.getStatus());
        ApiResponse<String> apiResponse = ApiResponse.error(errorCode.getStatus(), errorCode.getMessage(), errorCode);
        response.getWriter().write(apiResponse.toJson());
    }

    private static void setResponse(HttpServletResponse response, HttpStatus status) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(status.value());
    }

    /**
     * db안의 리프레쉬 토큰이 있을 경우 ture 리턴 없으면 null 반환 함수
     *
     * @param request
     * @param response
     * @param authentication
     * @return null or String(accessToken)
     * @작성자 : lbs
     * @작성일 : 2025-05-11
     * @설명 : db안의 리프레쉬 토큰이 있을 경우 ture 리턴
     */
    private String createNewAccessToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        String userName = jwtTokenProvider.getUserName(accessToken);
        String email = authentication.getName();
        String newAccessToken = jwtTokenProvider.generateAccessToken(email, userName, authentication.getAuthorities());
        return newAccessToken;
    }

    /**
     * 요청 헤더에 리프레쉬 토큰이 있는지 확인 함수
     * @작성자   : lbs
     * @작성일   : 2025-05-11
     * @설명     : 요청 헤더에 리프레쉬 토큰이 있는지 확인 함수
     * @param request
     * @param errorCode
     * @return ture or false
     */
    private boolean isMakeAccessToken(HttpServletRequest request, ErrorCode errorCode) {
        return errorCode == ErrorCode.EXPIRED_TOKEN && request.getHeader("refreshToken") != null;
    }

    /**
     * 에러 메시지로 에러 코드 가져오기
     * @param message
     * @return
     * @작성자   : lbs
     * @작성일   : 2025-05-11
     * @설명     : 에러 메시지로 에러 코드 가져오기
     */
    public ErrorCode findErrorCodeByMessage(String message) {
        ErrorCode errorCode = Arrays.stream(ErrorCode.values()).filter(c -> c.getMessage().equals(message)).findFirst().orElse(ErrorCode.UNKNOWN_ERROR);
        return errorCode;
    }
}




