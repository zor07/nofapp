package com.zor07.nofapp.profile.repository;

import com.zor07.nofapp.profile.entity.UserProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProgressRepository extends JpaRepository<UserProgressEntity, Long> {
}
