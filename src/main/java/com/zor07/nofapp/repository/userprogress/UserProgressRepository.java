package com.zor07.nofapp.repository.userprogress;

import com.zor07.nofapp.entity.userprogress.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    UserProgress findByUserId(Long userId);

}
