package com.zor07.nofapp.repository.profile;

import com.zor07.nofapp.model.profile.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    //TODO add test
    ProfileEntity getProfileByUserId(Long userId);

}
