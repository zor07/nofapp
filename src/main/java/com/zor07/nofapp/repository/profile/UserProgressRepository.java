package com.zor07.nofapp.repository.profile;

import com.zor07.nofapp.entity.profile.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    UserProgress findByUserId(Long userId);

}
