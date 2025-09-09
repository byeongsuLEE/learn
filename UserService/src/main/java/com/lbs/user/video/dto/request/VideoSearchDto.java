package com.lbs.user.video.dto.request;

/**
 * 작성자  : lbs
 * 날짜    : 2025-09-04
 * 풀이방법
 **/


public record VideoSearchDto(
    String title, String description, Long userId,  String tag,String keyword) {}
