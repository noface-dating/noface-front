package com.duri.durifront.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duri.durifront.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
