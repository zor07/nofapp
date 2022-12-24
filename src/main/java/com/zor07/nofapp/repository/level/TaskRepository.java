package com.zor07.nofapp.repository.level;

import com.zor07.nofapp.entity.levels.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByLevelId(Long levelId);

}
