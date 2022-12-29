package com.zor07.nofapp.service.level;

import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.service.levels.LevelService;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.LevelTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LevelServiceTest extends AbstractApplicationTest {

    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private LevelService levelService;

    @BeforeClass
    @AfterTest
    void clearDb() {
        levelRepository.deleteAll();
    }

    @Test
    void saveTest() {
        levelRepository.deleteAll();
        final var all = levelRepository.findAll();
        assertThat(all).isEmpty();

        final var level = LevelTestUtils.getBlankEntity();

        levelService.save(level);

        final var result = levelRepository.findAll();
        assertThat(result).hasSize(1);
        LevelTestUtils.checkEntity(level, result.get(0), false);
    }

    @Test
    void getAllTest() {
        levelService.save(LevelTestUtils.getBlankEntity());
        levelService.save(LevelTestUtils.getBlankEntity());
        levelService.save(LevelTestUtils.getBlankEntity());

        final var result = levelService.getAll();

        assertThat(result).hasSize(3);
        assertThat(result).allSatisfy( level ->
                LevelTestUtils.checkEntity(LevelTestUtils.getBlankEntity(), level, false)
        );
    }
    @Test
    void deleteTest() {

    }

//    @Test
//    void testCrud() {
//        levelRepository.deleteAll();
//        final var all = levelRepository.findAll();
//        assertThat(all).isEmpty();
//
//        final var level = LevelTestUtils.getBlankEntity();
//
//        final var id = levelRepository.save(level).getId();
//        final var inserted = levelRepository.findById(id).get();
//        LevelTestUtils.checkEntity(level, inserted, false);
//        LevelTestUtils.updateEntity(inserted);
//
//        levelRepository.save(inserted);
//
//        final var updated = levelRepository.findById(id).get();
//        LevelTestUtils.checkUpdated(inserted);
//
//        levelRepository.delete(updated);
//
//        assertThat(levelRepository.findById(id)).isEmpty();
//    }
}
