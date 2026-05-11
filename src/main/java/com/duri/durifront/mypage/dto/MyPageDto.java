package com.duri.durifront.mypage.dto;

import lombok.*;

import java.util.List;

/**
 * 마이페이지 화면에 전달되는 사용자 프로필 정보 DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageDto {

    /** 사용자 닉네임 */
    private String nickname;

    /** 나이 (생년월일 기반 계산) */
    private Integer age;

    /** 거주 지역 */
    private String region;

    /** 자기소개 */
    private String intro;

    /** 아바타 이미지 URL */
    private String avatarUrl;

    /** 관심사(취미) 목록 */
    private List<String> interests;
}
