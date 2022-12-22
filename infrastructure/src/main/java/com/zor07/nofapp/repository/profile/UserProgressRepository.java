package com.zor07.nofapp.repository.profile;

import com.zor07.nofapp.model.profile.UserProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProgressRepository extends JpaRepository<UserProgressEntity, Long> {
}
