package com.zor07.nofapp.repository.level;

import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.FileTestUtils;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskContentTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskRepositoryTest extends AbstractApplicationTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskContentRepository taskContentRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private FileRepository fileRepository;

    @BeforeClass
    @AfterTest
    void clearDb() {
        taskRepository.deleteAll();
        taskContentRepository.deleteAll();
        fileRepository.deleteAll();
        levelRepository.deleteAll();
    }

    @Test
    void testCrud() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
        final var task = TaskTestUtils.getBlankEntity(taskContent, level);
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
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level1));
        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level1));
        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level1));
        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level2));

        assertThat(taskRepository.findAllByLevelId(level1.getId())).hasSize(3);
        assertThat(taskRepository.findAllByLevelId(level2.getId())).hasSize(1);
    }
}