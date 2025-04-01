package com.lbs.user.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


/**
 *
 *
 */
@Getter
@AllArgsConstructor
@JsonPropertyOrder({"code", "message", "data"})
public class ApiResponse<T> {

    private final HttpStatus status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL) // data가 null이면 JSON에서 제외
    private final T data;



    /**
     *
     * @ 작성자   : 이병수
     * @ 작성일   : 2025-03-02
     * @ 설명     : 요청 성공 시 데이터
     * @param message
     * @param data
     * @return 상태, 메시지, 데이터
     *
     */
    public static <T> ApiResponse<T> success (HttpStatus status, String message, T data){
        return new ApiResponse<>(status,message,data);
    }

    /**
     *
     * @ 작성자   : 이병수
     * @ 작성일   : 2025-03-02
     * @ 설명     : 요청 성공 시 메시지 없이 보내기
     * @param status
     * @param data
     * @return
     * @param <T>
     */
    public static <T> ApiResponse<T> success (HttpStatus status,T data){
        return new ApiResponse<>(status,null,data);
    }

    /**
     * 요청 실패 시 에러 반환 메서드
     *
     * @param
     * @param status
     * @param message
     * @param errorCode
     * @return
     * @ 작성자   : 이병수
     * @ 작성일   : 2025-03-02
     * @ 설명     : 요청 실패 시 에러 반환 메서드
     */
    public static <T> ApiResponse<T> error(HttpStatus status, String message, ErrorCode errorCode) {
        return new ApiResponse<T>(errorCode.getStatus(),errorCode.getMessage(),null);
    }


    /**
     *  카프카로 Apireponse 결과 값을 보낼 떄 json 사용
     * @ 작성자   : 이병수
     * @ 작성일   : 2025-03-02
     * @ 설명     : 데이터를 json으로 변환 함수
     * @return
     */
    public String toJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

}
