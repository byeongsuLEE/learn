package com.lbs.user.user.controller;

import com.lbs.user.user.domain.UserSettings;
import com.lbs.user.user.dto.request.UserSettingRequestDto;
import com.lbs.user.user.dto.response.UserResponseDto;
import com.lbs.user.user.dto.response.UserSettingResponseDto;
import com.lbs.user.user.mapper.UserMapper;
import com.lbs.user.common.response.ApiResponse;
import com.lbs.user.user.domain.User;
import com.lbs.user.user.dto.request.UserJoinRequestDto;
import com.lbs.user.user.service.UserService;
import com.lbs.user.user.service.UserSettingService;
import io.micrometer.core.annotation.Timed;
import jakarta.ws.rs.PUT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.lbs.user.common.response.ApiResponse.success;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-02
 * 풀이방법
 **/
@RefreshScope
@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final Environment env;
    private final UserService userService ;
    private final UserSettingService userSettingService;

    private final UserMapper  userMapper;

    @Value("${yml-name}")
    private String configFileName ;


    @GetMapping("/welcome")
    @Timed(value ="users.welcome", longTask = true)
    public ResponseEntity<ApiResponse<String>> gatewayConnectTest (){

        log.info("들어왔나?");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,"테스트 성공"));

    }
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<String>> gatewayCustomFilterTest (){

        log.info("customfilter check ");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,"customfilter success "));

    }
    @GetMapping("/port-check")
    @Timed(value ="users.status", longTask = true)
    public ResponseEntity<ApiResponse<String>> gatewayPortCheck (){

        log.info("customfilter check ");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,
                        String.format("서버 포트는 :"+ env.getProperty("local.server.port"))
                                + "\n config 파일 이름은 :  = " + configFileName
                ));
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<UserResponseDto>> joinUser (@RequestBody UserJoinRequestDto userJoinRequestDto){
        User user = userMapper.userJoinDtoToDomain(userJoinRequestDto);
        User savedUser = userService.joinUser(user);
        UserResponseDto userResponseDto = userMapper.userDomainToUserDto(savedUser);
        log.info(user.toString());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,"가입성공",userResponseDto));
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> userInfo(@PathVariable Long id){
        User user = userService.readUser(id);
        UserResponseDto userResponseDto = userMapper.userDomainToUserDto(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,userResponseDto));
    }


    @GetMapping("/{userId}/settings")
    public ResponseEntity<ApiResponse<UserSettingResponseDto>> userSetting(@PathVariable Long userId){
        UserSettings userSetting = userSettingService.getSettings(userId);
        UserSettingResponseDto userSettingResponseDto = userSetting.userSettingToDto();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,userSettingResponseDto));
    }

    @PostMapping("/{userId}/settings")
    public ResponseEntity<ApiResponse<UserSettingResponseDto>> userSetting(@PathVariable Long userId, @RequestBody UserSettingRequestDto userSettingRequestDto){
       UserSettings userSetting =  userSettingService.createUserSettings(userId,userSettingRequestDto);
       UserSettingResponseDto userSettingResponseDto = userSetting.userSettingToDto();
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(ApiResponse.success(HttpStatus.CREATED,userSettingResponseDto));
    }

    @PatchMapping("/{userId}/settings")
    public ResponseEntity<ApiResponse<UserSettingResponseDto>> updateUserSetting(@PathVariable Long userId, @RequestBody UserSettingRequestDto userSettingRequestDto){
        UserSettings userSetting =  userSettingService.updateSettings(userId,userSettingRequestDto);
        UserSettingResponseDto userSettingResponseDto = userSetting.userSettingToDto();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,userSettingResponseDto));
    }


}
