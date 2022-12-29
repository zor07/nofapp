package com.zor07.nofapp.service.levels;

import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.repository.level.TaskRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;

    public TaskService(final TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> getAllByLevelId(final Long levelId) {
        return repository.findAllByLevelId(levelId);
    }

    public Task getTask(final Long levelId, final Long taskId) {
        return repository.findByLevelIdAndId(levelId, taskId);
    }


    public Task save(final Task task) {
        return repository.save(task);
    }

    public void delete(final Long levelId, final Long id) {
        repository.deleteByLevelIdAndId(levelId, id);
    }

}
