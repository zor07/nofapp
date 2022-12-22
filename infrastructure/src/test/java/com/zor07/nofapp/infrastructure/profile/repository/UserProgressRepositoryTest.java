package com.zor07.nofapp.infrastructure.profile.repository;//package com.zor07.nofapp.infrastructure.repository.profile.repository;
//
//import com.zor07.nofapp.entity.levels.Task;
//import com.zor07.nofapp.profile.repository.UserProgressRepository;
//import com.zor07.nofapp.spring.AbstractApplicationTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.testng.annotations.AfterTest;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//public class UserProgressRepositoryTest extends AbstractApplicationTest {
//
//    @Autowired
//    private TaskRepository taskRepository;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private RoleRepository roleRepository;
//    @Autowired
//    private TaskContentRepository taskContentRepository;
//    @Autowired
//    private LevelRepository levelRepository;
//    @Autowired
//    private FileRepository fileRepository;
//    @Autowired
//    private UserProgressRepository userProgressRepository;
//
//
//    @BeforeClass
//    @AfterTest
//    void clearDb() {
//        userProgressRepository.deleteAll();
//        taskRepository.deleteAll();
//        taskContentRepository.deleteAll();
//        fileRepository.deleteAll();
//        levelRepository.deleteAll();
//        userRepository.deleteAll();
//        roleRepository.deleteAll();
//    }
//
//    @Test
//    void testCrud() {
//        var user = userRepository.save(UserTestUtils.createUser());
//        var task = taskRepository.save(createTask());
//        userProgressRepository.deleteAll();
//
//        final var all = userProgressRepository.findAll();
//        assertThat(all).isEmpty();
//
//        final var userProgress = UserProgresTestUtils.getBlankEntity(user, task);
//
//        final var id = userProgressRepository.save(userProgress).getId();
//        final var inserted = userProgressRepository.findById(id).get();
//        UserProgresTestUtils.checkEntity(userProgress, inserted, false);
//
//        final var newTask = taskRepository.save(TaskTestUtils.updateEntity(createTask()));
//        inserted.setCurrentTask(newTask);
//
//        userProgressRepository.save(inserted);
//
//        final var updated = userProgressRepository.findById(id).get();
//        UserProgresTestUtils.checkUpdated(updated);
//
//        userProgressRepository.delete(updated);
//
//        assertThat(userProgressRepository.findById(id)).isEmpty();
//    }
//
//    private Task createTask() {
//        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
//        final var file = fileRepository.save(FileTestUtils.getBlankEntity());
//        final var taskContent = taskContentRepository.save(TaskContentTestUtils.getBlankEntity(file));
//        return TaskTestUtils.getBlankEntity(taskContent, level);
//    }
//}
