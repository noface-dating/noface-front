package com.duri.durifront.like.repository;

import com.duri.durifront.like.entity.UserMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {
}
