package com.zor07.nofapp.service.level;

import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.service.levels.LevelService;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.LevelTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LevelServiceTest extends AbstractApplicationTest {

    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private LevelService levelService;

    @BeforeMethod
    @AfterClass
    void clearDb() {
        levelRepository.deleteAll();
    }

    @Test
    void findNextLevelTest_shouldReturnNextLevel() {
        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));
        final var level3 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(30));

        final var result = levelService.findNextLevel(level1);

        assertThat(result.getOrder()).isEqualTo(20);
    }

    @Test
    void findNextLevelTest_shouldReturnNull() {
        final var level1 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        final var level2 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));
        final var level3 = levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(30));

        assertThat(levelService.findNextLevel(level3)).isNull();
    }

    @Test
    void findFirstLevelTest_shouldReturnFirstLevel() {
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(30));

        final var result = levelService.findFirstLevel();

        assertThat(result.getOrder()).isEqualTo(10);
    }

    @Test
    void findPrevLevelTest_shouldReturnPrevLevel() {
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(30));

        final var result = levelRepository.findPrevLevel(20);

        assertThat(result.getOrder()).isEqualTo(10);
    }

    @Test
    void findPrevLevelTest_shouldReturnNull() {
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(10));
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(20));
        levelRepository.save(LevelTestUtils.getBlankEntityWithOrder(30));

        assertThat(levelRepository.findPrevLevel(10)).isNull();
    }

    @Test
    void findFirstLevelTest_shouldReturnNull() {
        assertThat(levelService.findFirstLevel()).isNull();
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
        levelRepository.save(LevelTestUtils.getBlankEntity());
        levelRepository.save(LevelTestUtils.getBlankEntity());
        levelRepository.save(LevelTestUtils.getBlankEntity());

        final var result = levelService.getAll();

        assertThat(result).hasSize(3);
        assertThat(result).allSatisfy( level ->
                LevelTestUtils.checkEntity(LevelTestUtils.getBlankEntity(), level, false)
        );
    }
    @Test
    void deleteTest() {
        assertThat(levelRepository.findAll()).isEmpty();
        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());

        levelService.delete(level.getId());
        assertThat(levelRepository.findAll()).isEmpty();
    }

}
