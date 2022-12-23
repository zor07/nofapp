package com.zor07.nofapp.repository.user;

import com.zor07.nofapp.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  User findByUsername(String username);

}
