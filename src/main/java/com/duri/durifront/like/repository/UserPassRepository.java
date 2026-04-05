package com.duri.durifront.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duri.durifront.like.entity.UserPass;

@Repository
public interface UserPassRepository extends JpaRepository<UserPass, Long> {

}
