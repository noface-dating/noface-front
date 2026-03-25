package com.duri.durifront.user.service;

import com.duri.durifront.user.entity.User;
import com.duri.durifront.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User createUser(String username,
                           String rawPassword,
                           String email
    )
    {
        // TODO: PasswordEncoder 적용
        User user = User.createUser(username, rawPassword, email);
        return userRepository.save(user);
    }
}
