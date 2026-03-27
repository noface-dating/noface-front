package com.duri.durifront.profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duri.durifront.profile.entity.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

	Optional<Profile> findByUser_UserId(Long userId);

	/**
	 * 추천 대상 프로필 조회:
	 * - 자기 자신 제외
	 * - 이미 좋아요/싫어요 한 프로필 제외
	 * - absolute_score 내림차순
	 */
	@Query("""
		SELECT p FROM Profile p
		WHERE p.user.userId <> :userId
		  AND p.gender <> :gender
		  AND p.user.userId NOT IN (
		    SELECT l.toUser.userId FROM UserLike l WHERE l.fromUser.userId = :userId
		  )
		  AND p.user.userId NOT IN (
		    SELECT up.toUser.userId FROM UserPass up WHERE up.fromUser.userId = :userId AND up.passedAt > :threeDaysAgo
		  )
		ORDER BY p.absoluteScore DESC
		""")
	List<Profile> findRecommendedProfiles(@Param("userId") Long userId, @Param("gender") Boolean gender, @Param("threeDaysAgo") java.time.LocalDateTime threeDaysAgo);
}
