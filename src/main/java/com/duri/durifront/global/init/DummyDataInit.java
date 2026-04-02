package com.duri.durifront.global.init;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.duri.durifront.like.entity.UserLike;
import com.duri.durifront.like.repository.UserLikeRepository;
import com.duri.durifront.profile.entity.Profile;
import com.duri.durifront.profile.repository.ProfileRepository;
import com.duri.durifront.user.entity.User;
import com.duri.durifront.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@org.springframework.context.annotation.Profile({"dev", "local"})
@RequiredArgsConstructor
public class DummyDataInit implements CommandLineRunner {

	private final UserRepository userRepository;
	private final ProfileRepository profileRepository;
	private final UserLikeRepository userLikeRepository;

	@Override
	public void run(String... args) throws Exception {
		if (userRepository.count() > 0) {
			System.out.println("Dummy data already initialized.");
			return;
		}
		System.out.println("Initializing Dummy Data for H2 database...");

		// 1. Me (Male)
		User me = createUser("me_user", "1234", "me@test.com");
		createProfile(me, "민수", LocalDate.of(1999, 5, 12), true, "서울, 강남구", "[\"#음악\", \"#여행\", \"#영화\"]", "ABCDE12345", "FGHIJ67890", (byte) 78, "새로운 사람들과 대화하는 것을 좋아해요!");

		// 2. Female 1 (서아)
		User user1 = createUser("user1", "1234", "user1@test.com");
		createProfile(user1, "서아", LocalDate.of(2001, 3, 20), false, "서울 강남구", "[\"#강아지\", \"#카페\", \"#영화\"]", "FGHIJ67890", "ABCDE12345", (byte) 85, "강아지 좋아해요! 주말에 카페에서 책 읽는 거 좋아해요.");

		// 3. Female 2 (수진)
		User user2 = createUser("user2", "1234", "user2@test.com");
		createProfile(user2, "수진", LocalDate.of(2000, 11, 3), false, "경기 성남시", "[\"#영화\", \"#여행\", \"#카페\"]", "ABCDF12346", "ABCDE12345", (byte) 90, "영화 보는 걸 좋아해요. 같이 여행 갈 사람 찾아요!");

		// 4. Female 3 (예린)
		User user3 = createUser("user3", "1234", "user3@test.com");
		createProfile(user3, "예린", LocalDate.of(2002, 7, 19), false, "서울 종로구", "[\"#카페투어\", \"#사진\", \"#독서\"]", "FGHIK67891", "ABCDE12345", (byte) 82, "감성적인 사진 찍고 책 읽는 걸 좋아해요.");
		
		// 5. Male 1 (지훈)
		User user4 = createUser("user4", "1234", "user4@test.com");
		createProfile(user4, "지훈", LocalDate.of(1997, 8, 15), true, "서울 마포구", "[\"#테니스\", \"#맛집\", \"#음악\"]", "KLMNO11111", "PQRST22222", (byte) 72, "주말엔 운동하고 맛집 탐방해요.");

		// 6. Like from 서아(user1) to 민수(me)
		UserLike like = UserLike.builder()
			.fromUser(user1)
			.toUser(me)
			.likedAt(LocalDateTime.now())
			.build();
		userLikeRepository.save(like);
	}

	private User createUser(String username, String password, String email) {
		User user = User.createUser(username, password, email);
		return userRepository.save(user);
	}

	private Profile createProfile(User user, String nickname, LocalDate birth, Boolean gender, String region, String hobbies, String face, String pref, byte score, String intro) {
		String additionalInfo = String.format("{\"mbti\":\"ENFP\",\"intro\":\"%s\"}", intro);
		Profile profile = Profile.createProfile(
			user, nickname, birth, gender, region, hobbies, face, pref, additionalInfo, score
		);
		return profileRepository.save(profile);
	}
}
