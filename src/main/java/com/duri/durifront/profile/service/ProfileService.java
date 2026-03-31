package com.duri.durifront.profile.service;

import com.duri.durifront.profile.entity.Profile;
import com.duri.durifront.profile.repository.ProfileRepository;
import com.duri.durifront.user.entity.User;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public Profile createProfile(User user,
                                 String nickname,
                                 LocalDate birthDate,
                                 Boolean gender,
                                 String region,
                                 String hobbies,
                                 String faceFeatures,
                                 String facePreference,
                                 String additionalInfo,
                                 Byte absoluteScore)
    {
        Profile profile = Profile.createProfile(
                user,
                nickname,
                birthDate,
                gender,
                region,
                hobbies,
                faceFeatures,
                facePreference,
                additionalInfo,
                absoluteScore);

        return profileRepository.save(profile);
    }
}
