package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.Practice;
import com.zor07.nofapp.entity.PracticeTag;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class PracticeRepositoryTest extends AbstractApplicationTest {
    private static final String TAG_NAME = "tag";
    private static final String PRACTICE_NAME = "practice";
    private static final String PRACTICE_NAME_NEW = "new practice";
    private static final String PRACTICE_DESCRIPTION = "description";
    private static final String PRACTICE_DATA = "{\"data\": \"value\"}";
    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private PracticeTagRepository tagRepository;

    @AfterClass
    @BeforeMethod
    void cleanUp() {
        practiceRepository.deleteAll();
        tagRepository.deleteAll();
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
    void testCrud() throws IOException {
        final var objectMapper = new ObjectMapper();
        final var practice = createPractice(true);

        final var id = practiceRepository.save(practice).getId();
        final var inserted = practiceRepository.findById(id).get();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getPracticeTag().getName()).isEqualTo(TAG_NAME);
        assertThat(inserted.getName()).isEqualTo(PRACTICE_NAME);
        assertThat(inserted.getDescription()).isEqualTo(PRACTICE_DESCRIPTION);
        assertThat(objectMapper.readTree(inserted.getData())).isEqualTo(objectMapper.readTree(PRACTICE_DATA));
        assertThat(inserted.isPublic()).isTrue();

        inserted.setName(PRACTICE_NAME_NEW);
        inserted.setPublic(true);
        practiceRepository.save(inserted);

        final var updated = practiceRepository.findById(id).get();
        assertThat(updated.getName()).isEqualTo(PRACTICE_NAME_NEW);
        assertThat(updated.isPublic()).isTrue();

        practiceRepository.delete(updated);

        assertThat(practiceRepository.findById(id)).isEmpty();
    }

    private void createPracticeTag() {
        final var practiceTag = new PracticeTag();
        practiceTag.setName(TAG_NAME);
        tagRepository.save(practiceTag);
    }

    private Practice createPractice(final boolean isPublic) {
        final var practice = new Practice();
        practice.setPracticeTag(tagRepository.findAll().get(0));
        practice.setName(PRACTICE_NAME);
        practice.setDescription(PRACTICE_DESCRIPTION);
        practice.setData(PRACTICE_DATA);
        practice.setPublic(isPublic);
        return practice;
    }
}
