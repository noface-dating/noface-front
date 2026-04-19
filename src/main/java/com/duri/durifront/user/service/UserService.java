package com.duri.durifront.user.service;

import com.duri.durifront.user.entity.User;
import com.duri.durifront.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User createUser(String username,
                           String rawPassword,
                           String email
    )
    {
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
        User user = User.createUser(username, encodedPassword, email);

        return userRepository.save(user);
    }

    public boolean isUsernameDuplicated(String username) {
        return userRepository.existsByUsername(username);
    }
}
