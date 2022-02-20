package com.zor07.nofapp.practice;

import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PracticeTagRepositoryTest extends AbstractApplicationTest {

    @Autowired
    private PracticeTagRepository practiceTagRepository;

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
