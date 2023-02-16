package com.zor07.nofapp.test;

import com.zor07.nofapp.api.v1.dto.level.LevelDto;
import com.zor07.nofapp.api.v1.dto.level.TaskDto;
import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.entity.level.Task;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskTestUtils {

    public static final Integer ORDER = 1;
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";

    public static final Integer NEW_ORDER = 2;
    public static final String NEW_NAME = "new name";
    public static final String NEW_DESCRIPTION = "new description";

    public static Task getBlankEntity(final Level level) {
        return getBlankEntity(null, level);
    }

    public static Task getBlankEntityWithOrder(final Level level, final Integer order) {
        final var entity = getBlankEntity(null, level);
        entity.setOrder(order);
        return entity;
    }

    public static TaskDto getBlankDto(
            final Long id,
            final LevelDto level
    ) {
        return new TaskDto(id, NAME, DESCRIPTION, ORDER, level);
    }

    public static Task getBlankEntity(
            final Long id,
            final Level level
    ) {
        return new Task(
                id,
                level,
                ORDER,
                NAME,
                DESCRIPTION
        );
    }

    public static Task updateEntity(final Task task) {
        task.setOrder(NEW_ORDER);
        task.setName(NEW_NAME);
        task.setDescription(NEW_DESCRIPTION);
        return task;
    }

    public static void checkUpdated(final Task task) {
        assertThat(task.getOrder()).isEqualTo(NEW_ORDER);
        assertThat(task.getName()).isEqualTo(NEW_NAME);
        assertThat(task.getDescription()).isEqualTo(NEW_DESCRIPTION);
    }

    public static void checkEntity(
            final Task actual,
            final Task expected,
            final boolean checkId
    ) {
        if (checkId) {
            assertThat(actual.getId()).isEqualTo(expected.getId());
        }
        assertThat(actual.getOrder()).isEqualTo(expected.getOrder());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
    }

}
