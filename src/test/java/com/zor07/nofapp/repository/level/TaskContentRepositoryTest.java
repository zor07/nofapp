package com.zor07.nofapp.repository.level;

import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.FileTestUtils;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskContentTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskContentRepositoryTest extends AbstractApplicationTest {

    @Autowired
    private TaskContentRepository taskContentRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private TaskRepository taskRepository;

    @BeforeMethod
    @AfterClass
    void clearDb() {
        taskContentRepository.deleteAll();
        taskRepository.deleteAll();
        levelRepository.deleteAll();
        fileRepository.deleteAll();
    }

    @Test
    void testCrud() {
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(level));
        final var taskContent = TaskContentTestUtils.getBlankEntity(task, file);
        final var all = taskContentRepository.findAll();
        assertThat(all).isEmpty();

        final var id = taskContentRepository.save(taskContent).getId();
        final var inserted = taskContentRepository.findById(id).get();
        TaskContentTestUtils.checkEntity(inserted, taskContent, false);

        TaskContentTestUtils.updateEntity(inserted);
        taskContentRepository.save(inserted);

        final var updated = taskContentRepository.findById(id).get();
        TaskContentTestUtils.checkUpdated(updated);

        taskContentRepository.delete(updated);

        assertThat(taskContentRepository.findById(id)).isEmpty();
    }

    @Test
    void deleteByTaskIdTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(level));
        taskContentRepository.save(TaskContentTestUtils.getBlankEntity(task, file));
        taskContentRepository.save(TaskContentTestUtils.getBlankEntity(task, file));
        taskContentRepository.save(TaskContentTestUtils.getBlankEntity(task, file));

        taskContentRepository.findAllByTaskId(task.getId());

        final var all = taskContentRepository.findAll();
        assertThat(all).hasSize(3);

        final var resultTaskContent = all.get(0);
        assertThat(resultTaskContent.getId()).isNotNull();
        assertThat(resultTaskContent.getTitle()).isEqualTo(TaskContentTestUtils.TITLE);
        assertThat(resultTaskContent.getData()).isEqualTo(TaskContentTestUtils.DATA);
        assertThat(resultTaskContent.getOrder()).isEqualTo(TaskContentTestUtils.ORDER);

        final var resultTask = resultTaskContent.getTask();
        assertThat(resultTask.getId()).isEqualTo(task.getId());
        assertThat(resultTask.getOrder()).isEqualTo(task.getOrder());
        assertThat(resultTask.getName()).isEqualTo(task.getName());

        final var resultLevel = resultTask.getLevel();
        assertThat(resultLevel.getId()).isEqualTo(level.getId());
        assertThat(resultLevel.getOrder()).isEqualTo(level.getOrder());
        assertThat(resultLevel.getName()).isEqualTo(level.getName());
    }
}
