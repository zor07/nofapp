package com.zor07.nofapp.api.v1.controller.level;

import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.service.levels.LevelService;
import com.zor07.nofapp.spring.AbstractApiTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;

public class LevelControllerTest extends AbstractApiTest {

    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private LevelService levelService;

    @BeforeClass
    @AfterTest
    void clearDb() {
        levelRepository.deleteAll();
    }

//    @Test
//    void saveTest() {
//        levelRepository.deleteAll();
//        final var all = levelRepository.findAll();
//        assertThat(all).isEmpty();
//
//        final var level = LevelTestUtils.getBlankEntity();
//
//        levelService.save(level);
//
//        final var result = levelRepository.findAll();
//        assertThat(result).hasSize(1);
//        LevelTestUtils.checkEntity(level, result.get(0), false);
//    }
//
//    @Test
//    void getAllTest() {
//        levelRepository.save(LevelTestUtils.getBlankEntity());
//        levelRepository.save(LevelTestUtils.getBlankEntity());
//        levelRepository.save(LevelTestUtils.getBlankEntity());
//
//        final var result = levelService.getAll();
//
//        assertThat(result).hasSize(3);
//        assertThat(result).allSatisfy( level ->
//                LevelTestUtils.checkEntity(LevelTestUtils.getBlankEntity(), level, false)
//        );
//    }
//    @Test
//    void deleteTest() {
//        assertThat(levelRepository.findAll()).isEmpty();
//        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
//
//        levelService.delete(level.getId());
//        assertThat(levelRepository.findAll()).isEmpty();
//    }

}