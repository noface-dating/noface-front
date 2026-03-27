package com.duri.durifront.mypage.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageDto {
    private String nickname;
    private Integer age;
    private String region;
    private String intro;
    private String avatarUrl;
    private List<String> interests;
}
