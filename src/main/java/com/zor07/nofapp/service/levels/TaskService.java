package com.zor07.nofapp.service.levels;

import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;
    private final LevelRepository levelRepository;

    public TaskService(final TaskRepository repository,
                       final LevelRepository levelRepository) {
        this.repository = repository;
        this.levelRepository = levelRepository;
    }

    public List<Task> getAllByLevelId(final Long levelId) {
        return repository.findAllByLevelId(levelId);
    }

    public Task getTask(final Long levelId, final Long taskId) {
        return repository.findByLevelIdAndId(levelId, taskId);
    }


    @Transactional
    public Task save(Long levelId, final Task task) {
        final var level = levelRepository.getById(levelId);
        task.setLevel(level);
        return repository.save(task);
    }

    public void delete(final Long levelId, final Long id) {
        repository.deleteByLevelIdAndId(levelId, id);
    }

}
