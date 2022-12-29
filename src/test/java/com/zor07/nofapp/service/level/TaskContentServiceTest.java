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

    private static final String TASK_BUCKET = "task";
    private static final String TASK_FILE_KEY= "task_file";

    @BeforeClass
    void setUp() {
        s3.createBucketIfNotExists(TASK_BUCKET);
        tearDown();
    }

    @AfterTest
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
    void saveTest() {
        final var file = fileRepository.save(FileTestUtils.getBlankEntity(TASK_BUCKET));
        final var taskContent = TaskContentTestUtils.getBlankEntity(file);

        taskContentService.save(taskContent);

        final var result = taskContentRepository.findAll();
        assertThat(result).hasSize(1);
        TaskContentTestUtils.checkEntity(result.get(0), taskContent, false);
    }

    @Test
    void deleteByLevelIdAndTaskIdTest() throws IOException {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity(TASK_BUCKET));
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
    void addVideoTest() throws IOException {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(null));
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));

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

        taskContentService.addVideo(level.getId(), task.getId(), multipartFile);

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
