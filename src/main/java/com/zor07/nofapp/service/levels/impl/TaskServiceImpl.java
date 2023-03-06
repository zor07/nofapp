package com.zor07.nofapp.service.levels.impl;

import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.service.levels.LevelService;
import com.zor07.nofapp.service.levels.TaskService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final LevelService levelService;

    public TaskServiceImpl(final TaskRepository repository,
                           final LevelService levelService) {
        this.repository = repository;
        this.levelService = levelService;
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
        final var level = levelService.findById(levelId);
        task.setLevel(level);
        return repository.save(task);
    }

    @Override
    public void delete(final Long levelId, final Long id) {
        repository.deleteByLevelIdAndId(levelId, id);
    }

    @Override
    public Task findNextTask(final Task task) {
        final var currentLevel = task.getLevel();
        var nextTask = repository.findNextTaskOfLevel(currentLevel.getId(), task.getOrder());
        if (nextTask == null) {
            final var nextLevel = levelService.findNextLevel(currentLevel);
            if (nextLevel == null) {
                return null;
            }
            nextTask = findFirstTaskOfLevel(nextLevel);
        }

        return nextTask;
    }

    @Override
    public Task findPrevTask(final Task task) {
        final var currentLevel = task.getLevel();
        var prevTask = repository.findPrevTaskOfLevel(currentLevel.getId(), task.getOrder());
        if (prevTask == null) {
            final var prevLevel = levelService.findPrevLevel(currentLevel);
            if (prevLevel == null) {
                return null;
            }
            prevTask = findLastTaskOfLevel(prevLevel);
        }

        return prevTask;
    }

    @Override
    public Task findFirstTaskOfLevel(final Level level) {
        return repository.findFirstTaskOfLevel(level.getId());
    }

    private Task findLastTaskOfLevel(final Level level) {
        return repository.findLastTaskOfLevel(level.getId());
    }

    private Task findNextTaskOfLevel(final Level levelId, final Task task) {
        return repository.findNextTaskOfLevel(levelId.getId(), task.getOrder());
    }

    private Task findPrevTaskOfLevel(final Level level, final Task task) {
        return repository.findPrevTaskOfLevel(level.getId(), task.getOrder());
    }
}
