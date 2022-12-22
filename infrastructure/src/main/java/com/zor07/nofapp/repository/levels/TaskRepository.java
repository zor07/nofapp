package com.zor07.nofapp.repository.levels;

import com.zor07.nofapp.model.levels.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}
