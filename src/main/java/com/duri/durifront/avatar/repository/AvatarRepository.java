package com.duri.durifront.avatar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duri.durifront.avatar.entity.Avatar;
import com.duri.durifront.avatar.entity.AvatarId;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, AvatarId> {

	Optional<Avatar> findByIdProfileId(Long profileId);

	Optional<Avatar> findByIdUserId(String userId);
}
