package com.zor07.nofapp.repository.level;

import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.LevelTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LevelRepositoryTest extends AbstractApplicationTest {

    @Autowired
    private LevelRepository levelRepository;

    @BeforeMethod
    @AfterClass
    void clearDb() {
        levelRepository.deleteAll();
    }

    @Test
    void findNextLevelTest_shouldReturnNextLevel() {
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(30));

        final var result = levelRepository.findNextLevel(10);

        assertThat(result.getOrder()).isEqualTo(20);
    }

    @Test
    void findNextLevelTest_shouldReturnNull() {
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(30));

        assertThat(levelRepository.findNextLevel(30)).isNull();
    }

    @Test
    void findFirstLevelTest_shouldReturnFirstLevel() {
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(30));

        final var result = levelRepository.findFirstLevel();

        assertThat(result.getOrder()).isEqualTo(10);
    }

    @Test
    void findFirstLevelTest_shouldReturnNull() {
        assertThat(levelRepository.findFirstLevel()).isNull();
    }

    @Test
    void testCrud() {
        levelRepository.deleteAll();
        final var all = levelRepository.findAll();
        assertThat(all).isEmpty();

        final var level = LevelTestUtils.getBlankEntity();

        final var id = levelRepository.save(level).getId();
        final var inserted = levelRepository.findById(id).get();
        LevelTestUtils.checkEntity(level, inserted, false);
        LevelTestUtils.updateEntity(inserted);

        levelRepository.save(inserted);

        final var updated = levelRepository.findById(id).get();
        LevelTestUtils.checkUpdated(inserted);

        levelRepository.delete(updated);

        assertThat(levelRepository.findById(id)).isEmpty();
    }
}
