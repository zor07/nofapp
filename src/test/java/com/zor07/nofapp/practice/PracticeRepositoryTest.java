package com.zor07.nofapp.practice;

import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PracticeRepositoryTest extends AbstractApplicationTest {

    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private PracticeTagRepository tagRepository;

    @Test
    void testCrud() {

        final var practiceTag = new PracticeTag();
        practiceTag.setName("tag");
        final var tagId = tagRepository.save(practiceTag).getId();
        final var tag = tagRepository.getById(tagId);

        practiceRepository.deleteAll();
        final var all = practiceRepository.findAll();
        assertThat(all).isEmpty();

        final var practice = new Practice();
        practice.setPracticeTag(tag);
        practice.setName("practice");
        practice.setDescription("description");
        practice.setData("data");

        final var id = practiceRepository.save(practice).getId();
        final var inserted = practiceRepository.findById(id).get();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getPracticeTag().getName()).isEqualTo("tag");
        assertThat(inserted.getName()).isEqualTo("practice");
        assertThat(inserted.getDescription()).isEqualTo("description");
        assertThat(inserted.getData()).isEqualTo("data");
        assertThat(inserted.isPublic()).isFalse();

        inserted.setName("new name");
        inserted.setPublic(true);
        practiceRepository.save(inserted);

        final var updated = practiceRepository.findById(id).get();
        assertThat(updated.getName()).isEqualTo("new name");
        assertThat(updated.isPublic()).isTrue();

        practiceRepository.delete(updated);

        assertThat(practiceRepository.findById(id)).isEmpty();
    }

}
