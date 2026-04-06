package com.duri.durifront.web;

import java.util.List;

/** React {@code mock.js} 의 {@code interestCategories} 와 동일 */
public final class InterestCatalog {

    public record Category(String id, String title, String icon, List<String> tags) {}

    public static final List<Category> ALL = List.of(
            new Category(
                    "lifestyle",
                    "라이프스타일 & 일상",
                    "☀️",
                    List.of(
                            "자기계발",
                            "미라클 모닝",
                            "독서",
                            "뉴스레터 읽기",
                            "집순이/집돌이",
                            "넷플릭스",
                            "유튜브",
                            "호캉스",
                            "드라이브",
                            "강아지",
                            "고양이",
                            "산책")),
            new Category(
                    "food",
                    "음식 & 맛집",
                    "🍽️",
                    List.of(
                            "카페 투어",
                            "카공",
                            "빵지순례",
                            "홈카페",
                            "혼술",
                            "와인",
                            "위스키",
                            "수제맥주",
                            "이자카야",
                            "마라탕",
                            "오마카세",
                            "떡볶이",
                            "비건",
                            "파인 다이닝")),
            new Category(
                    "activity",
                    "운동 & 액티비티",
                    "💪",
                    List.of(
                            "헬스 (오운완)",
                            "필라테스",
                            "요가",
                            "러닝",
                            "테니스",
                            "골프",
                            "배드민턴",
                            "축구/풋살",
                            "등산",
                            "클라이밍",
                            "서핑",
                            "보드/스키",
                            "캠핑")),
            new Category(
                    "culture",
                    "문화 & 예술",
                    "🎨",
                    List.of(
                            "애니메이션",
                            "웹툰",
                            "영화 감상",
                            "드라마 정주행",
                            "전시회",
                            "사진 촬영",
                            "LP 감상",
                            "악기 연주",
                            "뮤지컬/연극",
                            "K-POP",
                            "인디음악",
                            "재즈",
                            "힙합",
                            "밴드")));

    private InterestCatalog() {}
}
