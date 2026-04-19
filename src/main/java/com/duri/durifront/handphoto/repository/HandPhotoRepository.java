package com.duri.durifront.handphoto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duri.durifront.handphoto.entity.HandPhoto;
import com.duri.durifront.handphoto.entity.HandPhotoId;

@Repository
public interface HandPhotoRepository extends JpaRepository<HandPhoto, HandPhotoId> {

	Optional<HandPhoto> findByIdProfileIdAndStatus(Long profileId, HandPhoto.HandPhotoStatus status);

	Optional<HandPhoto> findByIdUserId(String userId);
}
