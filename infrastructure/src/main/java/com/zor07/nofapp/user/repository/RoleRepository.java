package com.zor07.nofapp.user.repository;

import com.zor07.nofapp.user.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

  RoleEntity findByName(String name);

}
