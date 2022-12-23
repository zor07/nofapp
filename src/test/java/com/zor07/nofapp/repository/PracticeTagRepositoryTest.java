package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.practice.PracticeTag;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PracticeTagRepositoryTest extends AbstractApplicationTest {

    @Autowired
    private PracticeTagRepository practiceTagRepository;

    @Autowired
    private PracticeRepository practiceRepository;

    @BeforeMethod
    @AfterClass
    void clearDb() {
        practiceRepository.deleteAll();
        practiceTagRepository.deleteAll();
    }

    @Test
    void testCrud() {

        practiceTagRepository.deleteAll();
        final var all = practiceTagRepository.findAll();
        assertThat(all).isEmpty();

        final var practiceTag = new PracticeTag();
        practiceTag.setName("PracticeTagName");

        final var id = practiceTagRepository.save(practiceTag).getId();
        final var inserted = practiceTagRepository.findById(id).get();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getName()).isEqualTo("PracticeTagName");

        inserted.setName("NewPracticeTagName");
        practiceTagRepository.save(inserted);

        final var updated = practiceTagRepository.findById(id).get();
        assertThat(updated.getName()).isEqualTo("NewPracticeTagName");

        practiceTagRepository.delete(updated);

        assertThat(practiceTagRepository.findById(id)).isEmpty();
    }

}
