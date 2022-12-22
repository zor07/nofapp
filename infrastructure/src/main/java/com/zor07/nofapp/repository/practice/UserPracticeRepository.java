package com.zor07.nofapp.repository.practice;

import com.zor07.nofapp.model.practice.PracticeEntity;
import com.zor07.nofapp.model.practice.UserPracticeEntity;
import com.zor07.nofapp.model.practice.UserPracticeKeyEntity;
import com.zor07.nofapp.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPracticeRepository extends JpaRepository<UserPracticeEntity, UserPracticeKeyEntity> {

    List<UserPracticeEntity> findAllByUserId(Long userId);

    void deleteAllByPractice(PracticeEntity practice);

    void deleteByUserAndPractice(UserEntity user, PracticeEntity practice);

    UserPracticeEntity findByUserAndPractice(UserEntity user, PracticeEntity practice);
}
