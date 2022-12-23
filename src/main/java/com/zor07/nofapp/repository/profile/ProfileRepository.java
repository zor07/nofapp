package com.zor07.nofapp.repository.profile;

import com.zor07.nofapp.entity.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    //TODO add test
    Profile getProfileByUserId(Long userId);

}
