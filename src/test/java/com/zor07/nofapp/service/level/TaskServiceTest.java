package com.zor07.nofapp.service.level;

import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.service.levels.TaskService;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskServiceTest extends AbstractApplicationTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private TaskService taskService;

    @BeforeMethod
    @AfterClass
    void clearDb() {
        taskRepository.deleteAll();
        levelRepository.deleteAll();
    }

    @Test
    void getAllByLevelIdTest() {
        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntity());
        taskRepository.save(TaskTestUtils.getBlankEntity(level1));
        taskRepository.save(TaskTestUtils.getBlankEntity(level1));
        taskRepository.save(TaskTestUtils.getBlankEntity(level1));
        taskRepository.save(TaskTestUtils.getBlankEntity(level2));

        assertThat(taskService.getAllByLevelId(level1.getId())).hasSize(3);
        assertThat(taskService.getAllByLevelId(level2.getId())).hasSize(1);
    }

    @Test
    void getTaskTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var saved = taskRepository.save(TaskTestUtils.getBlankEntity(level));

        final var result = taskService.getTask(level.getId(), saved.getId());
        TaskTestUtils.checkEntity(result, saved, true);
    }

    @Test
    void saveTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = TaskTestUtils.getBlankEntity(level);

        final var saved = taskService.save(level.getId(), task);

        final var all = taskRepository.findAll();

        assertThat(all).hasSize(1);
        final var savedTask = all.get(0);
        assertThat(savedTask.getLevel().getId()).isEqualTo(level.getId());
        TaskTestUtils.checkEntity(savedTask, task, false);
        TaskTestUtils.checkEntity(savedTask, saved, true);
    }

    @Test
    void deleteTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var saved = taskRepository.save(TaskTestUtils.getBlankEntity(level));

        taskService.delete(level.getId(), saved.getId());

        assertThat(taskRepository.findAll()).isEmpty();
    }

    @Test
    void findFirstTaskOfLevel_shouldReturnFirstTask() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 10));
        taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 20));
        taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 30));

        final var result = taskService.findFirstTaskOfLevel(level);

        assertThat(result.getOrder()).isEqualTo(10);
    }

    @Test
    void findFirstTaskOfLevel_shouldReturnNull() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = TaskTestUtils.getBlankEntityWithOrder(level, 10);
        assertThat(
                taskService.findNextTask(task)
        ).isNull();
    }

    @Test
    void findNextTaskOfLevel_shouldReturnNextTask() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 30));

        final var result = taskService.findNextTask(task1);

        assertThat(result.getOrder()).isEqualTo(20);
    }

    @Test
    void findNextTaskOfLevel_shouldReturnNull() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 30));

        assertThat(
                taskService.findNextTask(task3)
        ).isNull();
    }

    @Test
    void findPrevTaskOfLevel_shouldReturnPrevTask() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 30));

        final var result = taskService.findPrevTask(task2);

        assertThat(result.getOrder()).isEqualTo(10);
    }

    @Test
    void findPrevTaskOfLevel_shouldReturnNull() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 30));

        assertThat(
                taskService.findPrevTask(task1)
        ).isNull();
    }

}
