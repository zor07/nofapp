package com.zor07.nofapp.api.v1.controller.level;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zor07.nofapp.api.v1.dto.level.TaskDto;
import com.zor07.nofapp.api.v1.dto.level.mapper.LevelMapper;
import com.zor07.nofapp.api.v1.dto.level.mapper.TaskContentMapper;
import com.zor07.nofapp.api.v1.dto.level.mapper.TaskMapper;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskContentRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.spring.AbstractApiTest;
import com.zor07.nofapp.test.FileTestUtils;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskContentTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.zor07.nofapp.test.UserTestUtils.DEFAULT_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaskControllerTest extends AbstractApiTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskContentRepository taskContentRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskContentMapper taskContentMapper;
    @Autowired
    private LevelMapper levelMapper;

    private static final String TASKS_ENDPOINT  = "/api/v1/levels/%s/tasks";
    private static final String TASK_ENDPOINT  = TASKS_ENDPOINT + "/%s";

    @BeforeMethod
    public void setup() {
        tearDown();
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
    }

    @Test
    void getAllByLevelIdTest() throws Exception {
        //given
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
        final var expectedTask = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));
        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));
        taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));

        //when
        final var content = mvc.perform(get(url(level.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        final var tasks = objectMapper.readValue(content, new TypeReference<List<TaskDto>>(){});
        assertThat(tasks).hasSize(3);
        assertThat(tasks).allSatisfy( task -> {
            //task
            assertThat(task.id()).isNotNull();
            assertThat(task.name()).isEqualTo(expectedTask.getName());
            assertThat(task.description()).isEqualTo(expectedTask.getDescription());
            assertThat(task.order()).isEqualTo(expectedTask.getOrder());
            //level
            assertThat(task.level().id()).isNotNull();
            assertThat(task.level().name()).isEqualTo(level.getName());
            assertThat(task.level().order()).isEqualTo(level.getOrder());
            //content
            assertThat(task.taskContent().id()).isNotNull();
            assertThat(task.taskContent().data()).isEqualTo(objectMapper.readTree(taskContent.getData()));
            assertThat(task.taskContent().title()).isEqualTo(taskContent.getTitle());
            assertThat(task.taskContent().fileUri()).isEqualTo(String.format("%s/%s", file.getBucket(), file.getKey()));
        });
    }

    @Test
    void getTaskTest() throws Exception {
        //given
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
        final var expectedTask = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));

        //when
        final var content = mvc.perform(get(url(level.getId(), expectedTask.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        final var task = objectMapper.readValue(content, TaskDto.class);
        //task
        assertThat(task.id()).isNotNull();
        assertThat(task.name()).isEqualTo(expectedTask.getName());
        assertThat(task.description()).isEqualTo(expectedTask.getDescription());
        assertThat(task.order()).isEqualTo(expectedTask.getOrder());
        //level
        assertThat(task.level().id()).isNotNull();
        assertThat(task.level().name()).isEqualTo(level.getName());
        assertThat(task.level().order()).isEqualTo(level.getOrder());
        //content
        assertThat(task.taskContent().id()).isNotNull();
        assertThat(task.taskContent().data()).isEqualTo(objectMapper.readTree(taskContent.getData()));
        assertThat(task.taskContent().title()).isEqualTo(taskContent.getTitle());
        assertThat(task.taskContent().fileUri()).isEqualTo(String.format("%s/%s", file.getBucket(), file.getKey()));
    }

    @Test
    void createTaskTest() throws Exception {
        //given
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
        final var dto = TaskTestUtils.getBlankDto(null, taskContentMapper.toDto(taskContent), levelMapper.toDto(level));

        //when
        final var content = mvc.perform(post(url(level.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        //then
        final var task = objectMapper.readValue(content, TaskDto.class);
        //task
        assertThat(task.id()).isNotNull();
        assertThat(task.name()).isEqualTo(dto.name());
        assertThat(task.description()).isEqualTo(dto.description());
        assertThat(task.order()).isEqualTo(dto.order());
        //level
        assertThat(task.level().id()).isNotNull();
        assertThat(task.level().name()).isEqualTo(level.getName());
        assertThat(task.level().order()).isEqualTo(level.getOrder());
        //content
        assertThat(task.taskContent().id()).isNotNull();
        assertThat(task.taskContent().data()).isEqualTo(objectMapper.readTree(taskContent.getData()));
        assertThat(task.taskContent().title()).isEqualTo(taskContent.getTitle());
        assertThat(task.taskContent().fileUri()).isNull();

        final var taskFromDb = taskRepository.findById(task.id()).get();
        //task
        assertThat(taskFromDb.getId()).isNotNull();
        assertThat(taskFromDb.getName()).isEqualTo(dto.name());
        assertThat(taskFromDb.getDescription()).isEqualTo(dto.description());
        assertThat(taskFromDb.getOrder()).isEqualTo(dto.order());
        //level
        assertThat(taskFromDb.getLevel().getId()).isNotNull();
        assertThat(taskFromDb.getLevel().getName()).isEqualTo(level.getName());
        assertThat(taskFromDb.getLevel().getOrder()).isEqualTo(level.getOrder());
        //content
        assertThat(taskFromDb.getTaskContent().getId()).isNotNull();
        assertThat(taskFromDb.getTaskContent().getData()).isEqualTo(taskContent.getData());
        assertThat(taskFromDb.getTaskContent().getTitle()).isEqualTo(taskContent.getTitle());
        //file
        assertThat(taskFromDb.getTaskContent().getFile().getId()).isEqualTo(file.getId());
        assertThat(taskFromDb.getTaskContent().getFile().getKey()).isEqualTo(file.getKey());
        assertThat(taskFromDb.getTaskContent().getFile().getBucket()).isEqualTo(file.getBucket());
        assertThat(taskFromDb.getTaskContent().getFile().getMime()).isEqualTo(file.getMime());
        assertThat(taskFromDb.getTaskContent().getFile().getPrefix()).isEqualTo(file.getPrefix());
        assertThat(taskFromDb.getTaskContent().getFile().getSize()).isEqualTo(file.getSize());
    }

    @Test
    void updateTaskTest() throws Exception {
        //given
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
        final var expectedTask = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));
        final var newName = "new name";
        expectedTask.setName(newName);
        final var dto = taskMapper.toDto(expectedTask);

        //when
        final var content = mvc.perform(put(url(level.getId(), expectedTask.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();

        //then
        final var task = objectMapper.readValue(content, TaskDto.class);
        assertThat(task.id()).isNotNull();
        assertThat(task.name()).isEqualTo(newName);
        assertThat(task.description()).isEqualTo(dto.description());
        assertThat(task.order()).isEqualTo(dto.order());

        final var taskFromDb = taskRepository.findById(task.id()).get();
        assertThat(taskFromDb.getId()).isNotNull();
        assertThat(taskFromDb.getName()).isEqualTo(newName);
        assertThat(taskFromDb.getDescription()).isEqualTo(dto.description());
        assertThat(taskFromDb.getOrder()).isEqualTo(dto.order());
    }

    @Test
    void deleteTaskTest() throws Exception {
        //given
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
        final var task = taskRepository.save(TaskTestUtils.getBlankEntity(taskContent, level));

        //when
        mvc.perform(delete(url(level.getId(), task.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();

        //then
        assertThat(taskRepository.findAll()).isEmpty();
    }

    private String url(final Long levelId) {
        return String.format(TASKS_ENDPOINT, levelId);
    }

    private String url(final Long levelId, final Long taskId) {
        return String.format(TASK_ENDPOINT, levelId, taskId);
    }

}
