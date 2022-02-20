package com.zor07.nofapp.practice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPracticeRepository extends JpaRepository<UserPractice, UserPracticeKey> {

    List<UserPractice> findAllByUserId(Long userId);

}
