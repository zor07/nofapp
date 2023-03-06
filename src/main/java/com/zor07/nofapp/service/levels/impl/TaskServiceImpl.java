package com.zor07.nofapp.service.levels.impl;

import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.service.levels.TaskService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final LevelRepository levelRepository;

    public TaskServiceImpl(final TaskRepository repository,
                           final LevelRepository levelRepository) {
        this.repository = repository;
        this.levelRepository = levelRepository;
    }

    @Override
    public List<Task> getAllByLevelId(final Long levelId) {
        return repository.findAllByLevelId(levelId);
    }

    @Override
    public Task getTask(final Long levelId, final Long taskId) {
        return repository.findByLevelIdAndId(levelId, taskId);
    }

    @Override
    @Transactional
    public Task save(Long levelId, final Task task) {
        final var level = levelRepository.getById(levelId);
        task.setLevel(level);
        return repository.save(task);
    }

    @Override
    public void delete(final Long levelId, final Long id) {
        repository.deleteByLevelIdAndId(levelId, id);
    }

    @Override
    public Task findFirstTaskOfLevel(final Level level) {
        return repository.findFirstTaskOfLevel(level.getId());
    }

    @Override
    public Task findNextTaskOfLevel(Level levelId, Task task) {
        return repository.findNextTaskOfLevel(levelId.getId(), task.getOrder());
    }

    @Override
    public Task findPrevTaskOfLevel(Level level, Task task) {
        return repository.findPrevTaskOfLevel(level.getId(), task.getOrder());
    }
}
