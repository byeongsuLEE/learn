package com.lbs.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbs.user.user.mapper.UserMapper;
import com.lbs.user.user.controller.UserController;
import com.lbs.user.user.domain.User;
import com.lbs.user.user.dto.request.UserJoinRequestDto;
import com.lbs.user.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-26
 * rest docs 를 위한 테스트 클래스
 **/
@WebMvcTest(UserController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ImportAutoConfiguration(RefreshAutoConfiguration.class) // 스프링 cloud refresh 떄문에 씀
@ExtendWith(RestDocumentationExtension.class)
//@RequiredArgsConstructor : test class 에는 bean이 아니기 떄문에 spring 이 관리하지안흔다. 직접 주입 또는 @autowired하셈
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;

]


    @Test
    void SDFDS() throws Exception{
        LocalDateTime now = LocalDateTime.now();
        //given
        UserJoinRequestDto userJoinRequestDto = UserJoinRequestDto.builder()
                .email("test")
                .password("1234")
                .joinDate(now)
                .build();

        User user = User.builder()
                .email("test")
                .password("1234")
                .joinDate(now)
                .build();

        UserJoinResponseDto userJoinResponseDto = UserJoinResponseDto.builder()
                .email("test")
                .joinDate(now)
                .build();
        //when
        // mock 동작 정의  = Mock 객체들의 동작을 미리 정의해두면, 컨트롤러 테스트할 때 진짜 로직이 실행되지 않고 가짜(mock) 응답이 리턴돼.
        given(userMapper.userJoinDtoToDomain(any())).willReturn(user);
        given(userService.joinUser(any())).willReturn(user);
        given(userMapper.userToJoinResponseDto(any())).willReturn(userJoinResponseDto);

        //then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userJoinRequestDto))
                )
                .andExpect(status().isOk())
                .andDo(document("user-join",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                        )); // 문서 조각 생성
        //then
    }

}
