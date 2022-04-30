package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.Practice;
import com.zor07.nofapp.entity.UserPractice;
import com.zor07.nofapp.entity.UserPracticeKey;
import com.zor07.nofapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPracticeRepository extends JpaRepository<UserPractice, UserPracticeKey> {

    List<UserPractice> findAllByUserId(Long userId);

    void deleteAllByPractice(Practice practice);

    void deleteByUserAndPractice(User user, Practice practice);

    UserPractice findByUserAndPractice(User user, Practice practice);
}
