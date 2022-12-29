package com.zor07.nofapp.service.level;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskContentRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.service.levels.TaskContentService;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.FileTestUtils;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskContentTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.google.common.io.Files;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

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
    @Autowired
    private S3Service s3;

    @BeforeClass
    void setUp() {
        s3.createBucketIfNotExists(FileTestUtils.getBucket());
        tearDown();
    }

    @AfterTest
    void tearDown() {
        taskContentRepository.deleteAll();
        taskRepository.deleteAll();
        levelRepository.deleteAll();
        fileRepository.deleteAll();
        if (s3.containsBucket(FileTestUtils.getBucket())) {
            s3.truncateBucket(FileTestUtils.getBucket());
        }

    }

    @AfterClass
    void cleanS3() {
        if (s3.containsBucket(FileTestUtils.getBucket())) {
            s3.deleteBucket(FileTestUtils.getBucket());
        }
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
    void deleteByLevelIdAndTaskIdTest() throws IOException {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));

        final var srcFile = new java.io.File("src/test/resources/logback-test.xml");
        final var data = Files.toByteArray(srcFile);
        s3.persistObject(file.getBucket(), file.getKey(), data);

        assertThat(taskContentRepository.findAll()).isNotEmpty();
        assertThat(s3.findObjects(file.getBucket(), "")).isNotEmpty();

        taskContentService.deleteByLevelIdAndTaskId(level.getId(), task.getId());

        assertThat(taskContentRepository.findAll()).isEmpty();
        assertThat(s3.findObjects(file.getBucket(), "")).isEmpty();
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
