package com.duri.durifront.avatar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duri.durifront.avatar.entity.Avatar;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

	Optional<Avatar> findByProfileId(Long profileId);

	Optional<Avatar> findByUserId(String userId);
}
