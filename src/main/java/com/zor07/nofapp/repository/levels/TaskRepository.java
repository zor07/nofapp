package com.zor07.nofapp.repository.levels;

import com.zor07.nofapp.entity.levels.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
