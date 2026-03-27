package com.duri.durifront.repository;

import com.duri.durifront.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserUserId(Long userId);
}
