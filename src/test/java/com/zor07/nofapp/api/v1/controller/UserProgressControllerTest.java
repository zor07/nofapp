package com.zor07.nofapp.api.v1.controller;

import com.zor07.nofapp.api.v1.dto.userprogress.UserProgressDto;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.entity.userprogress.UserProgress;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.repository.user.UserRepository;
import com.zor07.nofapp.repository.userprogress.UserProgressRepository;
import com.zor07.nofapp.spring.AbstractApiTest;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;

import static com.zor07.nofapp.test.UserTestUtils.DEFAULT_USERNAME;
import static com.zor07.nofapp.test.UserTestUtils.createRole;
import static com.zor07.nofapp.test.UserTestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserProgressControllerTest extends AbstractApiTest {
    private static final String USER_PROGRESS_ENDPOINT = "/api/v1/progress";
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserProgressRepository userProgressRepository;

    @BeforeClass
    void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @BeforeMethod
    @AfterClass
    void clearDb() {
        userProgressRepository.deleteAll();
        taskRepository.deleteAll();
        fileRepository.deleteAll();
        levelRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void setNextTaskInUserProgress_shouldSetNextTaskOfSameLevel() throws Exception {
        //given
        final var roleName = persistRole();
        final var user = persistUser(DEFAULT_USERNAME, roleName);
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);

        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));

        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1,10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1,20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2,10));
        final var task4 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2,20));
        userProgressRepository.save(new UserProgress(user, task1));

        //when
        mvc.perform(put(USER_PROGRESS_ENDPOINT + "/nextTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();

        final var result = userProgressRepository.findCurrentUserProgress(user.getId());

        //then
        assertThat(result.getTask().getLevel().getOrder()).isEqualTo(task2.getLevel().getOrder());
        assertThat(result.getTask().getOrder()).isEqualTo(task2.getOrder());

        final var currentProgress = userProgressRepository.findCurrentUserProgress(user.getId());
        assertThat(currentProgress.getTask().getLevel().getOrder()).isEqualTo(task2.getLevel().getOrder());
        assertThat(currentProgress.getTask().getOrder()).isEqualTo(task2.getOrder());
    }

    @Test
    void setNextTaskInUserProgress_shouldSetNextTaskOfNextLevel() throws Exception {
        //given
        final var roleName = persistRole();
        final var user = persistUser(DEFAULT_USERNAME, roleName);
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);

        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));

        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1,10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1,20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2,10));
        final var task4 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2,20));
        userProgressRepository.save(new UserProgress(user, task2));

        //when
        mvc.perform(put(USER_PROGRESS_ENDPOINT + "/nextTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();

        final var result = userProgressRepository.findCurrentUserProgress(user.getId());

        //then
        assertThat(result.getTask().getLevel().getOrder()).isEqualTo(task3.getLevel().getOrder());
        assertThat(result.getTask().getOrder()).isEqualTo(task3.getOrder());

        final var currentProgress = userProgressRepository.findCurrentUserProgress(user.getId());
        assertThat(currentProgress.getTask().getLevel().getOrder()).isEqualTo(task3.getLevel().getOrder());
        assertThat(currentProgress.getTask().getOrder()).isEqualTo(task3.getOrder());
    }

    @Test
    void setNextTaskInUserProgress_shouldNotChangeUserProgressWhenNoMoreTasks() throws Exception {
        //given
        final var roleName = persistRole();
        final var user = persistUser(DEFAULT_USERNAME, roleName);
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);

        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));

        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1,10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level1,20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2,10));
        final var task4 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level2,20));
        userProgressRepository.save(new UserProgress(user, task4));

        //when
        mvc.perform(put(USER_PROGRESS_ENDPOINT + "/nextTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();

        final var result = userProgressRepository.findCurrentUserProgress(user.getId());

        //then
        assertThat(result.getTask().getLevel().getOrder()).isEqualTo(task4.getLevel().getOrder());
        assertThat(result.getTask().getOrder()).isEqualTo(task4.getOrder());

        final var currentProgress = userProgressRepository.findCurrentUserProgress(user.getId());
        assertThat(currentProgress.getTask().getLevel().getOrder()).isEqualTo(task4.getLevel().getOrder());
        assertThat(currentProgress.getTask().getOrder()).isEqualTo(task4.getOrder());
    }

    @Test
    void getUserProgressTest() throws Exception {
        //given
        final var roleName = persistRole();
        final var user = persistUser(DEFAULT_USERNAME, roleName);
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);

        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task1 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 10));
        final var task2 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 20));
        final var task3 = taskRepository.save(TaskTestUtils.getBlankEntityWithOrder(level, 30));
        userProgressRepository.save(new UserProgress(null, user, task1, Instant.now()));
        userProgressRepository.save(new UserProgress(null, user, task2, Instant.now()));
        userProgressRepository.save(new UserProgress(null, user, task3, null));

        //when
        final var content = mvc.perform(get(USER_PROGRESS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        final var result = objectMapper.readValue(content, UserProgressDto.class);

        assertThat(result.uncompletedTask().task().order()).isEqualTo(30);
        assertThat(result.userTasks()).hasSize(3);
        assertThat(result.userTasks().stream().filter(UserProgressDto.UserTaskDto::completed)).hasSize(2);
        assertThat(result.userTasks().stream().filter(it -> !it.completed())).hasSize(1);

    }

    private String persistRole() {
        final var role = createRole();
        roleRepository.save(role);
        return role.getName();
    }

    private User persistUser(final String name, final String roleName) {
        final var user = createUser(name);
        userService.saveUser(user);
        userService.addRoleToUser(user.getUsername(), roleName);
        return user;
    }

}
