package com.zor07.nofapp.api.v1.controller.level;

import com.zor07.nofapp.api.v1.dto.level.mapper.LevelMapper;
import com.zor07.nofapp.api.v1.dto.level.mapper.TaskContentMapper;
import com.zor07.nofapp.api.v1.dto.level.mapper.TaskMapper;
import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskContentRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.service.levels.TaskContentService;
import com.zor07.nofapp.spring.AbstractApiTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class TaskContentControllerTest extends AbstractApiTest {

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
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskContentMapper taskContentMapper;
    @Autowired
    private LevelMapper levelMapper;

    private static final String TASK_BUCKET = "task";
    private static final String TASK_FILE_KEY= "task_file";
    private static final String TASK_CONTENT_ENDPOINT  = "/api/v1/levels/%d/tasks/%d/content";
    private static final String TASK_CONTENT_VIDEO_ENDPOINT  = TASK_CONTENT_ENDPOINT + "/video";

    @BeforeClass
    void setUp() {
        s3.createBucketIfNotExists(TASK_BUCKET);
        tearDown();
    }

    @BeforeMethod
    public void setup() {
        tearDown();
        createDefaultUser();
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
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
    void createTaskContentTest() {
        //given

        //when

        //then
    }

    @Test
    void deleteTaskContentTest() {
        //given

        //when

        //then
    }


    @Test
    void uploadVideoTest() {
        //given

        //when
        // - /taskContentId

        //then
    }

    @Test
    void updateTaskContentTest() {
        //given

        //when
        // - /taskContentId

        //then
    }


    private String url(final Long levelId, final Long taskId) {
        return String.format(TASK_CONTENT_ENDPOINT, levelId, taskId);
    }

    private String videoUrl(final Long levelId, final Long taskId) {
        return String.format(TASK_CONTENT_VIDEO_ENDPOINT, levelId, taskId);
    }

//    @Test
//    void saveTest() {
//        final var file = fileRepository.save(FileTestUtils.getBlankEntity(TASK_BUCKET));
//        final var taskContent = TaskContentTestUtils.getBlankEntity(file);
//
//        taskContentService.save(taskContent);
//
//        final var result = taskContentRepository.findAll();
//        assertThat(result).hasSize(1);
//        TaskContentTestUtils.checkEntity(result.get(0), taskContent, false);
//    }
//
//    @Test
//    void deleteByLevelIdAndTaskIdTest() throws IOException {
//        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
//        final var file = fileRepository.save(FileTestUtils.getBlankEntity(TASK_BUCKET));
//        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
//        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));
//
//        final var srcFile = new java.io.File("src/test/resources/logback-test.xml");
//        final var data = Files.toByteArray(srcFile);
//        s3.persistObject(file.getBucket(), file.getKey(), data);
//
//        assertThat(taskContentRepository.findAll()).isNotEmpty();
//        assertThat(s3.findObjects(file.getBucket(), "")).isNotEmpty();
//
//        taskContentService.deleteByLevelIdAndTaskId(level.getId(), task.getId());
//
//        assertThat(taskContentRepository.findAll()).isEmpty();
//        assertThat(s3.findObjects(file.getBucket(), "")).isEmpty();
//    }
//
//    @Test
//    void addVideoTest() throws IOException {
//        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
//        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(null));
//        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));
//
//        final var srcFile = new java.io.File("src/test/resources/logback-test.xml");
//        final var data = Files.toByteArray(srcFile);
//        final var multipartFile= new MockMultipartFile(
//                "file",
//                "hello.txt",
//                MediaType.TEXT_PLAIN_VALUE,
//                data
//        );
//
//        assertThat(fileRepository.findAll()).isEmpty();
//        assertThat(s3.findObjects(TASK_BUCKET, "")).isEmpty();
//
//        taskContentService.addVideo(level.getId(), task.getId(), multipartFile);
//
//        final var updatedTaskContent = taskContentRepository.getById(taskContent.getId());
//        final var file = updatedTaskContent.getFile();
//        assertThat(file.getBucket()).isEqualTo(TASK_BUCKET);
//        assertThat(file.getPrefix()).isEqualTo(String.format("%s-%s", task.getLevel().getId(), task.getId()));
//        assertThat(file.getKey()).startsWith(String.format("%s/%s_", task.getId(), TASK_FILE_KEY));
//        assertThat(file.getMime()).isEqualTo(MediaType.TEXT_PLAIN_VALUE);
//        assertThat(file.getSize()).isEqualTo(multipartFile.getSize());
//
//        assertThat(s3.readObject(TASK_BUCKET, file.getKey())).containsExactly(data);
//    }

}
