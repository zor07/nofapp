package com.zor07.nofapp.service.userprogress.impl;

import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.entity.userprogress.UserProgress;
import com.zor07.nofapp.repository.userprogress.UserProgressRepository;
import com.zor07.nofapp.service.levels.LevelService;
import com.zor07.nofapp.service.levels.TaskService;
import com.zor07.nofapp.service.userprogress.UserProgressService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class UserProgressServiceImpl implements UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final TaskService taskService;
    private final LevelService levelService;

    public UserProgressServiceImpl(final UserProgressRepository userProgressRepository,
                                   final TaskService taskService,
                                   final LevelService levelService) {
        this.userProgressRepository = userProgressRepository;
        this.taskService = taskService;
        this.levelService = levelService;
    }

    @Override
    public void initUserProgress(final User user) {
        final var userProgress = userProgressRepository.findByUserId(user.getId());
        if (userProgress == null || userProgress.isEmpty()) {
            final var level = levelService.findFirstLevel();
            final var task = taskService.findFirstTaskOfLevel(level);
            userProgressRepository.save(new UserProgress(user, task));
        }
    }

    @Override
    @Transactional
    public UserProgress addNextTaskToUserProgress(final User user) {
        final var userProgress = userProgressRepository.findCurrentUserProgress(user.getId());
        addCompletedDatetimeToUserProgress(userProgress);

        final var currentTask = userProgress.getTask();
        var nextTask = taskService.findNextTask(currentTask);
        if (nextTask == null) {
            return null;
        }

        final var newUserProgress = new UserProgress(
                userProgress.getUser(),
                nextTask);
        return userProgressRepository.save(newUserProgress);
    }

    @Override
    public Task getCurrentTaskForUser(final User user) {
        var userProgress = userProgressRepository.findCurrentUserProgress(user.getId());
        if (userProgress == null) {
            initUserProgress(user);
            userProgress = userProgressRepository.findCurrentUserProgress(user.getId());
        }

        return userProgress.getTask();
    }

    @Override
    @Transactional
    public List<UserProgress> getUserProgress(User user) {
        final var userProgresses = userProgressRepository.findByUserId(user.getId());
        if (userProgresses == null || userProgresses.isEmpty()) {
            initUserProgress(user);
            return userProgressRepository.findByUserId(user.getId());
        }
        return userProgresses;
    }

    private void addCompletedDatetimeToUserProgress(final UserProgress userProgress) {
        userProgress.setCompletedDatetime(Instant.now());
        userProgressRepository.save(userProgress);
    }
}
