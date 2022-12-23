package com.zor07.nofapp.test;

import com.zor07.nofapp.entity.file.File;

import static org.assertj.core.api.Assertions.assertThat;

public class FileTestUtils {


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

    public static File getBlankEntity() {
        var file = new File();
        file.setId(null);
        file.setBucket(BUCKET_1);
        file.setPrefix(PREFIX_1);
        file.setKey(KEY_1);
        file.setMime(MIME_1);
        file.setSize(SIZE_1);
        return file;
    }

    public static void checkEntity(
            final File actual,
            final File expected,
            final boolean checkId
    ) {
        if (checkId) {
            assertThat(actual.getId()).isEqualTo(expected.getId());
        }
        assertThat(actual.getBucket()).isEqualTo(expected.getBucket());
        assertThat(actual.getPrefix()).isEqualTo(expected.getPrefix());
        assertThat(actual.getKey()).isEqualTo(expected.getKey());
        assertThat(actual.getMime()).isEqualTo(expected.getMime());
        assertThat(actual.getSize()).isEqualTo(expected.getSize());
    }
}
