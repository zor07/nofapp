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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.shaded.com.google.common.io.Files;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
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

    private static final String TASK_BUCKET = "task";
    private static final String TASK_FILE_KEY= "task_file";

    @BeforeClass
    void setUp() {
        s3.createBucketIfNotExists(TASK_BUCKET);
        tearDown();
    }

    @AfterMethod
    void tearDown() {
        taskContentRepository.deleteAll();
        taskRepository.deleteAll();
        levelRepository.deleteAll();
        fileRepository.deleteAll();
        if (s3.containsBucket(TASK_BUCKET)) {
            s3.truncateBucket(TASK_BUCKET);
        }
    }

    @AfterClass
    void cleanS3() {
        tearDown();
        if (s3.containsBucket(TASK_BUCKET)) {
            s3.deleteBucket(TASK_BUCKET);
        }
    }

    @Test
    void getTaskContentTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity(TASK_BUCKET));
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(null, level));
        taskContentRepository.save(TaskContentTestUtils.getBlankEntity(task, file));
        taskContentRepository.save(TaskContentTestUtils.getBlankEntity(task, file));
        taskContentRepository.save(TaskContentTestUtils.getBlankEntity(task, file));

        final var all = taskContentService.getTaskContent(level.getId(), task.getId());
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

    @Test
    void saveTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity(TASK_BUCKET));
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(null, level));
        final var taskContent = TaskContentTestUtils.getBlankEntity(task, file);

        taskContentService.save(level.getId(), task.getId(), taskContent);

        final var result = taskContentRepository.findAll();
        assertThat(result).hasSize(1);
        TaskContentTestUtils.checkEntity(result.get(0), taskContent, false);
    }

    @Test
    void updateTest() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity(TASK_BUCKET));
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(level));
        final var newTitle = "new title";
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(task, file));
        final var newTaskContent = TaskContentTestUtils.getBlankEntity(task, file);
        newTaskContent.setId(taskContent.getId());
        newTaskContent.setTitle(newTitle);

        taskContentService.update(level.getId(), task.getId(), newTaskContent);

        final var result = taskContentRepository.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(newTitle);
    }

    @Test
    void addVideoTest() throws IOException {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(level));
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(task, null));

        final var srcFile = new java.io.File("src/test/resources/logback-test.xml");
        final var data = Files.toByteArray(srcFile);
        final var multipartFile= new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                data
        );

        assertThat(fileRepository.findAll()).isEmpty();
        assertThat(s3.findObjects(TASK_BUCKET, "")).isEmpty();

        taskContentService.addVideo(level.getId(), task.getId(), taskContent.getId(), multipartFile);

        final var updatedTaskContent = taskContentRepository.getById(taskContent.getId());
        final var file = updatedTaskContent.getFile();
        assertThat(file.getBucket()).isEqualTo(TASK_BUCKET);
        assertThat(file.getPrefix()).isEqualTo(String.format("%s-%s", task.getLevel().getId(), task.getId()));
        assertThat(file.getKey()).startsWith(String.format("%s/%s_", task.getId(), TASK_FILE_KEY));
        assertThat(file.getMime()).isEqualTo(MediaType.TEXT_PLAIN_VALUE);
        assertThat(file.getSize()).isEqualTo(multipartFile.getSize());

        assertThat(s3.readObject(TASK_BUCKET, file.getKey())).containsExactly(data);
    }

}
