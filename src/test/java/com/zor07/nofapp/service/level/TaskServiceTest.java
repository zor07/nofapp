package com.zor07.nofapp.service.level;

import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskContentRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.service.levels.TaskService;
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

public class TaskServiceTest extends AbstractApplicationTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskContentRepository taskContentRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private TaskService taskService;

    @BeforeClass
    @AfterTest
    void clearDb() {
        taskRepository.deleteAll();
        taskContentRepository.deleteAll();
        fileRepository.deleteAll();
        levelRepository.deleteAll();
    }

    @Test
    void getAllByLevelIdTest() {
        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level1));
        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level1));
        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level1));
        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level2));

        assertThat(taskService.getAllByLevelId(level1.getId())).hasSize(3);
        assertThat(taskService.getAllByLevelId(level2.getId())).hasSize(1);
    }

    @Test
    void getTaskTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
        final var saved = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));

        final var result = taskService.getTask(level.getId(), saved.getId());
        TaskTestUtils.checkEntity(result, saved, true);
    }

    @Test
    void saveTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
        final var task = TaskTestUtils.getBlankEntity(taskContent, level);

        final var saved = taskService.save(task);

        final var all = taskRepository.findAll();

        assertThat(all).hasSize(1);
        TaskTestUtils.checkEntity(all.get(0), task, false);
        TaskTestUtils.checkEntity(all.get(0), saved, true);
    }

    @Test
    void deleteTest() {

    }



//    @Test
//    void findAllByLevelIdTest() {
//        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntity());
//        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntity());
//        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
//        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
//        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level1));
//        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level1));
//        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level1));
//        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level2));
//
//        assertThat(taskRepository.findAllByLevelId(level1.getId())).hasSize(3);
//        assertThat(taskRepository.findAllByLevelId(level2.getId())).hasSize(1);
//    }
//
//    @Test
//    void findByTaskContentIdTest() {
//        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
//        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
//        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
//        final var task = TaskTestUtils.getBlankEntity(taskContent, level);
//        taskRepository.save(task);
//
//        final var persisted = taskRepository.findByTaskContentId(taskContent.getId());
//        TaskTestUtils.checkEntity(persisted, task, false);
//    }
//
//    @Test
//    void findByLevelIdAndIdTest() {
//        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
//        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
//        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
//        final var task = TaskTestUtils.getBlankEntity(taskContent, level);
//        final var saved = taskRepository.save(task);
//
//        final var result = taskRepository.findByLevelIdAndId(level.getId(), saved.getId());
//        TaskTestUtils.checkEntity(result, task, false);
//    }
//
//    @Test
//    void deleteByLevelIdAndIdTest() {
//        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
//        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
//        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
//        final var task = TaskTestUtils.getBlankEntity(taskContent, level);
//        final var saved = taskRepository.save(task);
//
//        taskRepository.deleteByLevelIdAndId(level.getId(), saved.getId());
//
//        assertThat(taskRepository.findAll()).isEmpty();
//    }
}
