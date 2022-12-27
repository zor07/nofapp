package com.zor07.nofapp.service.levels;

import com.zor07.nofapp.repository.level.TaskContentRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class TaskContentService {

    private final TaskContentRepository taskContentRepository;

    public TaskContentService(TaskContentRepository taskContentRepository) {
        this.taskContentRepository = taskContentRepository;
    }


}
