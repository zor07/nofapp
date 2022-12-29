package com.zor07.nofapp.service.level;

import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskContentRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.service.levels.TaskContentService;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.FileTestUtils;
import com.zor07.nofapp.test.TaskContentTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskContentServiceTest extends AbstractApplicationTest {

    @Autowired
    private TaskContentRepository taskContentRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskContentService taskContentService;

    @BeforeClass
    @AfterTest
    void clearDb() {
        taskContentRepository.deleteAll();
        taskRepository.deleteAll();
        levelRepository.deleteAll();
        fileRepository.deleteAll();
    }

    @Test
    void saveTest() {
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var taskContent = TaskContentTestUtils.getBlankEntity(file);

        taskContentService.save(taskContent);

        final var result = taskContentRepository.findAll();
        assertThat(result).hasSize(1);
        TaskContentTestUtils.checkEntity(result.get(0), taskContent, false);

    }

    @Test
    void deleteByLevelIdAndTaskIdTest() {

    }

    @Test
    void addVideoTest() {

    }

//    @Test
//    void testCrud() {
//        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
//        final var taskContent = TaskContentTestUtils.getBlankEntity(file);
//        final var all = taskContentRepository.findAll();
//        assertThat(all).isEmpty();
//
//        final var id = taskContentRepository.save(taskContent).getId();
//        final var inserted = taskContentRepository.findById(id).get();
//        TaskContentTestUtils.checkEntity(inserted, taskContent, false);
//
//        TaskContentTestUtils.updateEntity(inserted);
//        taskContentRepository.save(inserted);
//
//        final var updated = taskContentRepository.findById(id).get();
//        TaskContentTestUtils.checkUpdated(updated);
//
//        taskContentRepository.delete(updated);
//
//        assertThat(taskContentRepository.findById(id)).isEmpty();
//    }
//
//    @Test
//    void deleteByTaskIdTest() {
//        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
//        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
//        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
//        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));
//
//        taskContentRepository.deleteByLevelIdAndTaskId(task.getLevel().getId(), task.getId());
//
//        assertThat(taskContentRepository.findAll()).isEmpty();
//    }
}
