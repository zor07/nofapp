package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
}
