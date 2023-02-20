package com.zor07.nofapp.service.levels;

import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.entity.level.Task;

import javax.transaction.Transactional;
import java.util.List;

public interface TaskService {
    List<Task> getAllByLevelId(Long levelId);

    Task getTask(Long levelId, Long taskId);

    @Transactional
    Task save(Long levelId, Task task);

    void delete(Long levelId, Long id);

    Task findFirstTaskOfLevel(Level level);

    Task findNextTaskOfLevel(Level levelId, Task task);
}
