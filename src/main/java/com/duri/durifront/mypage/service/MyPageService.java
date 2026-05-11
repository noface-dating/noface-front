package com.duri.durifront.mypage.service;

import com.duri.durifront.mypage.dto.MyPageDto;

/**
 * 마이페이지 비즈니스 로직 인터페이스.
 * <p>프로필 조회 및 수정 기능을 정의하며, 구현체 교체를 통해 core 서버 연동이 가능하다.</p>
 */
public interface MyPageService {

    /**
     * 사용자 프로필 정보를 조회한다.
     *
     * @param userId 사용자 ID
     * @return 마이페이지 DTO, 프로필이 없으면 기본값 반환
     */
    MyPageDto getMyPage(String userId);

    /**
     * 사용자 프로필을 수정한다.
     *
     * @param userId   사용자 ID
     * @param nickname 변경할 닉네임
     * @param region   변경할 지역
     * @param intro    변경할 자기소개 (null 허용)
     */
    void updateProfile(String userId, String nickname, String region, String intro);
}
