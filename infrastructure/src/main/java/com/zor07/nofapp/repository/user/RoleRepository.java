package com.zor07.nofapp.repository.user;

import com.zor07.nofapp.model.user.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

  RoleEntity findByName(String name);

}
