package com.zor07.nofapp.repository.userprogress;

import com.zor07.nofapp.entity.userprogress.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    List<UserProgress> findByUserId(Long userId);

    @Query(
            nativeQuery = true,
            value = """
                select *
                from user_progress
                order by id desc
                limit 1"""
    )
    UserProgress findCurrentUserProgress(Long userId);

}
