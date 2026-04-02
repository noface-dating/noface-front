package com.duri.durifront.like.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duri.durifront.like.entity.UserLike;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

	/** 중복 체크 및 매칭 체크: 상대방이 나에게 좋아요를 보냈는지 */
	Optional<UserLike> findByFromUser_UserIdAndToUser_UserId(String fromUserId, String toUserId);

	/** 오늘 좋아요 전송 수 */
	long countByFromUser_UserIdAndLikedAtAfter(String fromUserId, LocalDateTime after);

	/** 내가 보낸 좋아요 목록 */
	List<UserLike> findByFromUser_UserId(String fromUserId);

	/** 나에게 온 좋아요 목록 */
	List<UserLike> findByToUser_UserId(String toUserId);
}
