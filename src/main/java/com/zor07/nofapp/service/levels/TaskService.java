package com.zor07.nofapp.service.levels;

import com.zor07.nofapp.repository.level.TaskContentRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskContentRepository taskContentRepository;

    public TaskService(final TaskRepository taskRepository,
                       final TaskContentRepository taskContentRepository) {
        this.taskRepository = taskRepository;
        this.taskContentRepository = taskContentRepository;
    }
}
