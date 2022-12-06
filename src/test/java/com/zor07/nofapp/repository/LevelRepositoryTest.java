package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.levels.Level;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LevelRepositoryTest extends AbstractApplicationTest {

    private static final Integer ORDER = 1;
    private static final Integer NEW_ORDER = 2;
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
        level.setOrder(ORDER);

        final var id = levelRepository.save(level).getId();
        final var inserted = levelRepository.findById(id).get();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getName()).isEqualTo(NAME);
        assertThat(inserted.getOrder()).isEqualTo(ORDER);

        inserted.setName(NEW_NAME);
        inserted.setOrder(NEW_ORDER);
        levelRepository.save(inserted);

        final var updated = levelRepository.findById(id).get();
        assertThat(updated.getName()).isEqualTo(NEW_NAME);
        assertThat(inserted.getOrder()).isEqualTo(NEW_ORDER);

        levelRepository.delete(updated);

        assertThat(levelRepository.findById(id)).isEmpty();
    }
}
