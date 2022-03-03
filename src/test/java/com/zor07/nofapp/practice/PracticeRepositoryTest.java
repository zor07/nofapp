package com.zor07.nofapp.practice;

import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PracticeRepositoryTest extends AbstractApplicationTest {

    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private PracticeTagRepository tagRepository;

    @BeforeMethod
    void cleanUp() {
        practiceRepository.deleteAll();
        tagRepository.deleteAll();
        createPracticeTag();
        final var all = practiceRepository.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    void findAllByPublicTest() {
        practiceRepository.save(createPractice(true));
        practiceRepository.save(createPractice(true));
        practiceRepository.save(createPractice(true));
        practiceRepository.save(createPractice(false));
        practiceRepository.save(createPractice(false));
        practiceRepository.save(createPractice(false));

        assertThat(practiceRepository.findByIsPublic(true)).hasSize(3);
        assertThat(practiceRepository.findByIsPublic(false)).hasSize(3);
    }

    @Test
    void testCrud() {
        final var practice = createPractice(true);

        final var id = practiceRepository.save(practice).getId();
        final var inserted = practiceRepository.findById(id).get();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getPracticeTag().getName()).isEqualTo("tag");
        assertThat(inserted.getName()).isEqualTo("practice");
        assertThat(inserted.getDescription()).isEqualTo("description");
        assertThat(inserted.getData()).isEqualTo("data");
        assertThat(inserted.isPublic()).isTrue();

        inserted.setName("new name");
        inserted.setPublic(true);
        practiceRepository.save(inserted);

        final var updated = practiceRepository.findById(id).get();
        assertThat(updated.getName()).isEqualTo("new name");
        assertThat(updated.isPublic()).isTrue();

        practiceRepository.delete(updated);

        assertThat(practiceRepository.findById(id)).isEmpty();
    }

    private void createPracticeTag() {
        final var practiceTag = new PracticeTag();
        practiceTag.setName("tag");
        tagRepository.save(practiceTag);
    }

    private Practice createPractice(final boolean isPublic) {
        final var practice = new Practice();
        practice.setPracticeTag(tagRepository.findAll().get(0));
        practice.setName("practice");
        practice.setDescription("description");
        practice.setData("data");
        practice.setPublic(isPublic);
        return practice;
    }
}
