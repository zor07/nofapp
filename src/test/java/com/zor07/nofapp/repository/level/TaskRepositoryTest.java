package com.zor07.nofapp.repository.level;

import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskRepositoryTest extends AbstractApplicationTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private LevelRepository levelRepository;

    @BeforeMethod
    @AfterClass
    void clearDb() {
        taskRepository.deleteAll();
        levelRepository.deleteAll();
    }

    @Test
    void testCrud() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = TaskTestUtils.getBlankEntity(level);
        final var all = taskRepository.findAll();
        assertThat(all).isEmpty();

        final var id = taskRepository.save(task).getId();
        final var inserted = taskRepository.findById(id).get();
        TaskTestUtils.checkEntity(inserted, task, false);

        TaskTestUtils.updateEntity(inserted);
        taskRepository.save(inserted);

        final var updated = taskRepository.findById(id).get();
        TaskTestUtils.checkUpdated(updated);

        taskRepository.delete(updated);

        assertThat(taskRepository.findById(id)).isEmpty();
    }

    @Test
    void findAllByLevelIdTest() {
        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntity());
        taskRepository.save(TaskTestUtils.getBlankEntity(level1));
        taskRepository.save(TaskTestUtils.getBlankEntity(level1));
        taskRepository.save(TaskTestUtils.getBlankEntity(level1));
        taskRepository.save(TaskTestUtils.getBlankEntity(level2));

        assertThat(taskRepository.findAllByLevelId(level1.getId())).hasSize(3);
        assertThat(taskRepository.findAllByLevelId(level2.getId())).hasSize(1);
    }

    @Test
    void findByLevelIdAndIdTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = TaskTestUtils.getBlankEntity(level);
        final var saved = taskRepository.save(task);

        final var result = taskRepository.findByLevelIdAndId(level.getId(), saved.getId());
        TaskTestUtils.checkEntity(result, task, false);
    }

    @Test
    void deleteByLevelIdAndIdTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = TaskTestUtils.getBlankEntity(level);
        final var saved = taskRepository.save(task);

        taskRepository.deleteByLevelIdAndId(level.getId(), saved.getId());

        assertThat(taskRepository.findAll()).isEmpty();
    }
}
