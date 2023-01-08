package com.zor07.nofapp.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.api.v1.dto.level.TaskContentDto;
import com.zor07.nofapp.api.v1.dto.level.TaskDto;
import com.zor07.nofapp.entity.file.File;
import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.entity.level.TaskContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskContentTestUtils {


    public static final String TITLE = "title";
    public static final String DATA = "{\"data\": \"value\"}";

    public static final String NEW_TITLE = "title";
    public static final String NEW_DATA = "{\"data\": \"new value\"}";

    private static final String FILE_URI = "asdasd/asdads";
    private static final Integer ORDER = 1;

    public static TaskContentDto getBlankDto(final Long id, final TaskDto taskDto) throws IOException {
        return new TaskContentDto(
                id,
                taskDto,
                ORDER,
                TITLE,
                FILE_URI,
                new ObjectMapper().readTree(DATA)
        );
    }

    public static TaskContent getBlankEntity(final Task task,
                                             final File file) {
        return getBlankEntity(null, task, file);
    }

    public static TaskContent getBlankEntity(final Long id,
                                             final Task task,
                                             final File file) {
        return new TaskContent(
                id,
                task,
                ORDER,
                file,
                TITLE,
                DATA
        );
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
