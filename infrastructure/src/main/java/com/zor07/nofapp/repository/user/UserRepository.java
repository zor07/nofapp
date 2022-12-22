package com.zor07.nofapp.repository.user;

import com.zor07.nofapp.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  UserEntity findByUsername(String username);

}
