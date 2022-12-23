package com.zor07.nofapp.test;

import com.zor07.nofapp.entity.file.File;
import com.zor07.nofapp.entity.levels.TaskContent;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskContentTestUtils {

    public static final String TITLE = "title";
    public static final String DATA = "{\"data\": \"value\"}";

    public static final String NEW_TITLE = "title";
    public static final String NEW_DATA = "{\"data\": \"new value\"}";

    public static TaskContent getBlankEntity(final File file) {
        final var taskContent = new TaskContent();
        taskContent.setTitle(TITLE);
        taskContent.setData(DATA);
        taskContent.setFile(file);
        return taskContent;
    }

    public static void updateEntity(final TaskContent content) {
        content.setTitle(NEW_TITLE);
        content.setData(NEW_DATA);
    }

    public static void checkUpdated(final TaskContent content) {
        assertThat(content.getData()).isEqualTo(NEW_DATA);
        assertThat(content.getTitle()).isEqualTo(NEW_TITLE);
    }

    public static void checkEntity(
            final TaskContent actual,
            final TaskContent expected,
            final boolean checkId
    ) {
        if (checkId) {
            assertThat(actual.getId()).isEqualTo(expected.getId());
        }
        assertThat(actual.getData()).isEqualTo(expected.getData());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        FileTestUtils.checkEntity(actual.getFile(), expected.getFile(), checkId);
    }

}
