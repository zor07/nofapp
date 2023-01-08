package com.zor07.nofapp.repository.level;

import com.zor07.nofapp.entity.level.TaskContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskContentRepository extends JpaRepository<TaskContent, Long> {

    List<TaskContent> findAllByTaskId(Long taskId);
}


