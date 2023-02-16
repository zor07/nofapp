package com.zor07.nofapp.service.profile;

import com.zor07.nofapp.entity.profile.UserProgress;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.repository.profile.UserProgressRepository;
import com.zor07.nofapp.service.levels.LevelService;
import com.zor07.nofapp.service.levels.TaskService;
import org.springframework.stereotype.Service;

@Service
public class UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final TaskService taskService;
    private final LevelService levelService;

    public UserProgressService(final UserProgressRepository userProgressRepository,
                               final TaskService taskService,
                               final LevelService levelService) {
        this.userProgressRepository = userProgressRepository;
        this.taskService = taskService;
        this.levelService = levelService;
    }


    public UserProgress getUserProgress(final User user) {
        return userProgressRepository.findByUserId(user.getId());
    }

    public void initUserProgress(final User user) {
        if (userProgressRepository.findByUserId(user.getId()) == null) {
            final var level = levelService.findFirstLevel();
            final var task = taskService.findFirstTaskOfLevel(level);
            userProgressRepository.save(new UserProgress(user, task));
        }
    }

    public UserProgress setNextTaskInUserProgress(final User user) {
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
}
