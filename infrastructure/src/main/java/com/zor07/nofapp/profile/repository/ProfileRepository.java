package com.zor07.nofapp.profile.repository;

import com.zor07.nofapp.profile.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    //TODO add test
    ProfileEntity getProfileByUserId(Long userId);

}
