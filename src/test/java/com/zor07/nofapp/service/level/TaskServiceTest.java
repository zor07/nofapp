package com.zor07.nofapp.service.level;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.service.levels.TaskService;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.shaded.com.google.common.io.Files;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskServiceTest extends AbstractApplicationTest {

    private static final String TASK_BUCKET = "task";
    private static final String TASK_FILE_KEY= "task_file";

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private S3Service s3;

    @BeforeClass
    void setUp() {
        s3.createBucketIfNotExists(TASK_BUCKET);
        tearDown();
    }

    @BeforeMethod
    @AfterClass
    void tearDown() {
        taskRepository.deleteAll();
        levelRepository.deleteAll();
        fileRepository.deleteAll();
        if (s3.containsBucket(TASK_BUCKET)) {
            s3.truncateBucket(TASK_BUCKET);
        }
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

    @Test
    void addVideoTest() throws IOException {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(level));

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

        taskService.addVideo(level.getId(), task.getId(), multipartFile);

        final var updatedTask = taskRepository.getById(task.getId());
        final var file = updatedTask.getFile();
        assertThat(file.getBucket()).isEqualTo(TASK_BUCKET);
        assertThat(file.getPrefix()).isEqualTo(String.format("%s-%s", task.getLevel().getId(), task.getId()));
        assertThat(file.getKey()).startsWith(String.format("%s/%s_", task.getId(), TASK_FILE_KEY));
        assertThat(file.getMime()).isEqualTo(MediaType.TEXT_PLAIN_VALUE);
        assertThat(file.getSize()).isEqualTo(multipartFile.getSize());

        assertThat(s3.readObject(TASK_BUCKET, file.getKey())).containsExactly(data);
    }

}
