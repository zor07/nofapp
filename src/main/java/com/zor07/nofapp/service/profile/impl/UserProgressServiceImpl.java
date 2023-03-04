package com.zor07.nofapp.service.profile.impl;

import com.zor07.nofapp.entity.level.TaskContent;
import com.zor07.nofapp.entity.profile.UserProgress;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.repository.profile.UserProgressRepository;
import com.zor07.nofapp.service.levels.LevelService;
import com.zor07.nofapp.service.levels.TaskContentService;
import com.zor07.nofapp.service.levels.TaskService;
import com.zor07.nofapp.service.profile.UserProgressService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProgressServiceImpl implements UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final TaskService taskService;
    private final TaskContentService taskContentService;
    private final LevelService levelService;

    public UserProgressServiceImpl(final UserProgressRepository userProgressRepository,
                                   final TaskService taskService,
                                   final TaskContentService taskContentService,
                                   final LevelService levelService) {
        this.userProgressRepository = userProgressRepository;
        this.taskService = taskService;
        this.taskContentService = taskContentService;
        this.levelService = levelService;
    }

    @Override
    public void initUserProgress(final User user) {
        if (userProgressRepository.findByUserId(user.getId()) == null) {
            final var level = levelService.findFirstLevel();
            final var task = taskService.findFirstTaskOfLevel(level);
            userProgressRepository.save(new UserProgress(user, task));
        }
    }

    @Override
    public UserProgress updateUserProgressToNextTask(final User user) {
        final var userProgress = userProgressRepository.findByUserId(user.getId());
        final var currentTask = userProgress.getCurrentTask();
        final var currentLevel = currentTask.getLevel();
        var nextTask = taskService.findNextTaskOfLevel(currentLevel, currentTask);

        if (nextTask == null) {
            final var nextLevel = levelService.findNextLevel(currentLevel);
            if (nextLevel == null) {
                return null;
            }

            nextTask = taskService.findFirstTaskOfLevel(nextLevel);
        }

        final var newUserProgress = new UserProgress(
                userProgress.getId(),
                userProgress.getUser(),
                nextTask
        );
        return userProgressRepository.save(newUserProgress);
    }

    @Override
    public List<TaskContent> getCurrentTaskContentForUser(final User user) {
        var userProgress = userProgressRepository.findByUserId(user.getId());
        if (userProgress == null) {
            initUserProgress(user);
            userProgress = userProgressRepository.findByUserId(user.getId());
        }

        final var currentTask = userProgress.getCurrentTask();
        return taskContentService.getTaskContent(
                currentTask.getLevel().getId(),
                currentTask.getId()
        );
    }
}
