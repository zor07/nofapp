package com.zor07.nofapp.practice.repository;

import com.zor07.nofapp.practice.entity.PracticeEntity;
import com.zor07.nofapp.practice.entity.UserPracticeEntity;
import com.zor07.nofapp.practice.entity.UserPracticeKeyEntity;
import com.zor07.nofapp.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPracticeRepository extends JpaRepository<UserPracticeEntity, UserPracticeKeyEntity> {

    List<UserPracticeEntity> findAllByUserId(Long userId);

    void deleteAllByPractice(PracticeEntity practice);

    void deleteByUserAndPractice(UserEntity user, PracticeEntity practice);

    UserPracticeEntity findByUserAndPractice(UserEntity user, PracticeEntity practice);
}
