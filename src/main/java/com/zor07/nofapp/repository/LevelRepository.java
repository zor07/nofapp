package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.levels.Level;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository  extends JpaRepository<Level, Long> {
}
