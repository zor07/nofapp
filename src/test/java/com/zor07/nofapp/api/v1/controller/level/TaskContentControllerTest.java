package com.zor07.nofapp.api.v1.controller.level;

import com.zor07.nofapp.api.v1.dto.level.TaskContentDto;
import com.zor07.nofapp.api.v1.dto.level.mapper.TaskContentMapper;
import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskContentRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.spring.AbstractApiTest;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskContentTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.google.common.io.Files;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.zor07.nofapp.test.UserTestUtils.DEFAULT_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private S3Service s3;
    @Autowired
    private TaskContentMapper taskContentMapper;

    private static final String TASK_BUCKET = "task";
    private static final String TASK_FILE_KEY= "task_file";
    private static final String TASK_CONTENT_ENDPOINT  = "/api/v1/levels/%d/tasks/%d/content";
    private static final String TASK_CONTENT_ID_ENDPOINT  = TASK_CONTENT_ENDPOINT + "/%d";
    private static final String TASK_CONTENT_VIDEO_ENDPOINT  = TASK_CONTENT_ENDPOINT + "/video";

    @BeforeMethod
    public void setup() {
        tearDown();
        s3.createBucketIfNotExists(TASK_BUCKET);
        createDefaultUser();
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterClass
    void tearDown() {
        taskRepository.deleteAll();
        taskContentRepository.deleteAll();
        fileRepository.deleteAll();
        levelRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        if (s3.containsBucket(TASK_BUCKET)) {
            s3.truncateBucket(TASK_BUCKET);
        }
    }

    @Test
    void createTaskContentTest() throws Exception {
        //given
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(null, level));
        final var dto = new TaskContentDto(null, "title", null, null);

        //when
        mvc.perform(post(url(level.getId(), task.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        //then
        final var all = taskContentRepository.findAll();
        final var taskContent = all.get(0);
        assertThat(all).hasSize(1);
        assertThat(taskContent.getId()).isNotNull();
        assertThat(taskContent.getData()).isEqualTo("null");
        assertThat(taskContent.getFile()).isNull();
        assertThat(taskContent.getTitle()).isEqualTo(dto.title());

        final var updatedTask = taskRepository.findAll().get(0);
        assertThat(updatedTask.getTaskContent().getId()).isEqualTo(taskContent.getId());
    }

    @Test
    void deleteTaskContentTest() throws Exception  {
        //given
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(null));
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));

        //when
        mvc.perform(delete(url(level.getId(), task.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();

        //then
        assertThat(taskContentRepository.findAll()).isEmpty();
    }


    @Test
    void uploadVideoTest() throws Exception {
        //given
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(null));
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));
        final var srcFile = new java.io.File("src/test/resources/logback-test.xml");
        final var data = Files.toByteArray(srcFile);

        final var multipartFile = new MockMultipartFile(
                "file",
                "logback-test.xml",
                "text/plain",
                data
        );

        //when
        mvc.perform(multipart(videoUrl(level.getId(), task.getId()))
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        final var updatedTaskContent = taskContentRepository.getById(taskContent.getId());
        final var file = updatedTaskContent.getFile();
        assertThat(file.getBucket()).isEqualTo(TASK_BUCKET);
        assertThat(file.getPrefix()).isEqualTo(String.format("%s-%s", task.getLevel().getId(), task.getId()));
        assertThat(file.getKey()).startsWith(String.format("%s/%s_", task.getId(), TASK_FILE_KEY));
        assertThat(file.getMime()).isEqualTo(MediaType.TEXT_PLAIN_VALUE);
        assertThat(file.getSize()).isEqualTo(multipartFile.getSize());

        assertThat(s3.readObject(TASK_BUCKET, file.getKey())).containsExactly(data);
    }

    @Test
    void updateTaskContentTest() throws Exception  {
        //given
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var taskContentToSave = TaskContentTestUtils.getBlankEntity(null);
        taskContentToSave.setData(null);
        final var taskContent = taskContentRepository.save(taskContentToSave);
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));

        final var taskContentToUpdate = TaskContentTestUtils.getBlankEntity(null);
        taskContentToUpdate.setId(taskContent.getId());

        //when
        mvc.perform(put(urlId(level.getId(), task.getId(), taskContent.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskContentMapper.toDto(taskContentToUpdate)))
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();

        //then
        final var all = taskContentRepository.findAll();
        final var result = all.get(0);
        assertThat(all).hasSize(1);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getData()).isEqualTo(taskContentToUpdate.getData());
        assertThat(result.getFile()).isNull();
        assertThat(result.getTitle()).isEqualTo(taskContentToUpdate.getTitle());

        final var updatedTask = taskRepository.findAll().get(0);
        assertThat(updatedTask.getTaskContent().getId()).isEqualTo(result.getId());
    }


    private String url(final Long levelId, final Long taskId) {
        return String.format(TASK_CONTENT_ENDPOINT, levelId, taskId);
    }
    private String urlId(final Long levelId, final Long taskId, final Long taskContentId) {
        return String.format(TASK_CONTENT_ID_ENDPOINT, levelId, taskId, taskContentId);
    }

    private String videoUrl(final Long levelId, final Long taskId) {
        return String.format(TASK_CONTENT_VIDEO_ENDPOINT, levelId, taskId);
    }
}
