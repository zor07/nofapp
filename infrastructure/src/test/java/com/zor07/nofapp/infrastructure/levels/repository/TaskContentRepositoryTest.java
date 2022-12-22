package com.zor07.nofapp.infrastructure.levels.repository;//package com.zor07.nofapp.infrastructure.repository.levels.repository;
//
//import com.zor07.nofapp.levels.repository.TaskContentRepository;
//import com.zor07.nofapp.spring.AbstractApplicationTest;
//import com.zor07.nofapp.test.FileTestUtils;
//import com.zor07.nofapp.test.TaskContentTestUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//public class TaskContentRepositoryTest extends AbstractApplicationTest {
//
//    @Autowired
//    private TaskContentRepository taskContentRepository;
//
//    @Autowired
//    private FileRepository fileRepository;
//
//    @BeforeClass
//    @AfterClass
//    void clearDb() {
//        fileRepository.deleteAll();
//        taskContentRepository.deleteAll();
//    }
//
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
//}
