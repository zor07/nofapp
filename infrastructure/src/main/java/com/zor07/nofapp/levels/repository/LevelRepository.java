package com.zor07.nofapp.levels.repository;

import com.zor07.nofapp.levels.entity.LevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository  extends JpaRepository<LevelEntity, Long> {
}
