package com.zor07.nofapp.levels.repository;

import com.zor07.nofapp.levels.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}
