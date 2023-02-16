package com.zor07.nofapp.repository.profile;

import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.entity.profile.UserProgress;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.repository.level.TaskContentRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.repository.user.UserRepository;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import com.zor07.nofapp.test.UserProgresTestUtils;
import com.zor07.nofapp.test.UserTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProgressRepositoryTest extends AbstractApplicationTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TaskContentRepository taskContentRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserProgressRepository userProgressRepository;


    @BeforeMethod
    @AfterClass
    void clearDb() {
        userProgressRepository.deleteAll();
        taskRepository.deleteAll();
        taskContentRepository.deleteAll();
        fileRepository.deleteAll();
        levelRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void findByUserIdTest() {
        final var user = userRepository.save(UserTestUtils.createUser());
        final var task = taskRepository.save(createTaskWithOrder(777));
        userProgressRepository.save(new UserProgress(user, task));

        final var result = userProgressRepository.findByUserId(user.getId());

        assertThat(result.getCurrentTask().getOrder()).isEqualTo(777);
    }

    @Test
    void testCrud() {
        final var user = userRepository.save(UserTestUtils.createUser());
        final var task = taskRepository.save(createTask());
        userProgressRepository.deleteAll();

        final var all = userProgressRepository.findAll();
        assertThat(all).isEmpty();

        final var userProgress = UserProgresTestUtils.getBlankEntity(user, task);

        final var id = userProgressRepository.save(userProgress).getId();
        final var inserted = userProgressRepository.findById(id).get();
        UserProgresTestUtils.checkEntity(userProgress, inserted, false);

        final var newTask = taskRepository.save(TaskTestUtils.updateEntity(createTask()));
        inserted.setCurrentTask(newTask);

        userProgressRepository.save(inserted);

        final var updated = userProgressRepository.findById(id).get();
        UserProgresTestUtils.checkUpdated(updated);

        userProgressRepository.delete(updated);

        assertThat(userProgressRepository.findById(id)).isEmpty();
    }

    private Task createTask() {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        return TaskTestUtils.getBlankEntity(level);
    }

    private Task createTaskWithOrder(final Integer order) {
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
        final var task = TaskTestUtils.getBlankEntity(level);
        task.setOrder(order);
        return task;
    }
}
