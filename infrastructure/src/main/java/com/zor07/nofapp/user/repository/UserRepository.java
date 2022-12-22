package com.zor07.nofapp.user.repository;

import com.zor07.nofapp.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  UserEntity findByUsername(String username);

}
