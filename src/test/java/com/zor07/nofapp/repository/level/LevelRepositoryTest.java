package com.zor07.nofapp.repository.level;

import com.zor07.nofapp.spring.AbstractApplicationTest;
import com.zor07.nofapp.test.LevelTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LevelRepositoryTest extends AbstractApplicationTest {

    @Autowired
    private LevelRepository levelRepository;

    @BeforeClass
    @AfterTest
    void clearDb() {
        levelRepository.deleteAll();
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
