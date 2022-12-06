package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.levels.Level;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LevelRepositoryTest extends AbstractApplicationTest {

    private static final String NAME = "name";
    private static final String NEW_NAME = "new name";
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

        final var level = new Level();
        level.setName(NAME);

        final var id = levelRepository.save(level).getId();
        final var inserted = levelRepository.findById(id).get();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getName()).isEqualTo(NAME);

        inserted.setName(NEW_NAME);
        levelRepository.save(inserted);

        final var updated = levelRepository.findById(id).get();
        assertThat(updated.getName()).isEqualTo(NEW_NAME);

        levelRepository.delete(updated);

        assertThat(levelRepository.findById(id)).isEmpty();
    }
}
