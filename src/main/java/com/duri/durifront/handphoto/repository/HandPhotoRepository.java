package com.duri.durifront.handphoto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duri.durifront.handphoto.entity.HandPhoto;

@Repository
public interface HandPhotoRepository extends JpaRepository<HandPhoto, Long> {

	Optional<HandPhoto> findByProfileIdAndStatus(Long profileId, HandPhoto.HandPhotoStatus status);

	Optional<HandPhoto> findByUserId(Long userId);
}
