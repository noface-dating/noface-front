package com.duri.durifront.like.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duri.durifront.like.entity.UserPass;
import com.duri.durifront.like.repository.UserPassRepository;
import com.duri.durifront.user.entity.User;
import com.duri.durifront.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PassService {

    private final UserPassRepository userPassRepository;
    private final UserRepository userRepository;

    public void sendPass(Long fromUserId, Long toUserId) {
        User fromUser = userRepository.findById(fromUserId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid fromUserId"));
        User toUser = userRepository.findById(toUserId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid toUserId"));

        UserPass pass = UserPass.builder()
            .fromUser(fromUser)
            .toUser(toUser)
            .passedAt(LocalDateTime.now())
            .build();
            
        userPassRepository.save(pass);
    }
}
