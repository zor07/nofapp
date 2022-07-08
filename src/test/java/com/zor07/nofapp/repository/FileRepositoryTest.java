package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.File;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FileRepositoryTest extends AbstractApplicationTest {

    private static final String BUCKET_1 = "bucket_1";
    private static final String PREFIX_1 = "prefix_1";
    private static final String KEY_1 = "key_1";
    private static final String MIME_1 = "mime_1";
    private static final long SIZE_1 = 1L;
    private static final String BUCKET_2 = "bucket_2";
    private static final String PREFIX_2 = "prefix_2";
    private static final String KEY_2 = "key_2";
    private static final String MIME_2 = "mime_2";
    private static final long SIZE_2 = 2L;


    @Autowired
    private FileRepository fileRepository;


    @BeforeMethod
    @AfterClass
    void clearDb() {
        fileRepository.deleteAll();
    }

    @Test
    void testCrud() {

        fileRepository.deleteAll();
        final var all = fileRepository.findAll();
        assertThat(all).isEmpty();

        final var file = new File();
        file.setBucket(BUCKET_1);
        file.setPrefix(PREFIX_1);
        file.setKey(KEY_1);
        file.setMime(MIME_1);
        file.setSize(SIZE_1);

        final var id = fileRepository.save(file).getId();
        final var inserted = fileRepository.findById(id).get();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getBucket()).isEqualTo(BUCKET_1);
        assertThat(inserted.getPrefix()).isEqualTo(PREFIX_1);
        assertThat(inserted.getKey()).isEqualTo(KEY_1);
        assertThat(inserted.getMime()).isEqualTo(MIME_1);
        assertThat(inserted.getSize()).isEqualTo(SIZE_1);

        inserted.setBucket(BUCKET_2);
        inserted.setPrefix(PREFIX_2);
        inserted.setKey(KEY_2);
        inserted.setMime(MIME_2);
        inserted.setSize(SIZE_2);
        fileRepository.save(inserted);

        final var updated = fileRepository.findById(id).get();
        assertThat(updated).isNotNull();
        assertThat(updated.getBucket()).isEqualTo(BUCKET_2);
        assertThat(updated.getPrefix()).isEqualTo(PREFIX_2);
        assertThat(updated.getKey()).isEqualTo(KEY_2);
        assertThat(updated.getMime()).isEqualTo(MIME_2);
        assertThat(updated.getSize()).isEqualTo(SIZE_2);

        fileRepository.delete(updated);

        assertThat(fileRepository.findById(id)).isEmpty();
    }

}
