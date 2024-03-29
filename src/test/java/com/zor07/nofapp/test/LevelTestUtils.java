package com.zor07.nofapp.test;

import com.zor07.nofapp.api.v1.dto.level.LevelDto;
import com.zor07.nofapp.api.v1.dto.level.TaskDto;
import com.zor07.nofapp.entity.level.Level;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LevelTestUtils {

    public static final Integer ORDER = 1;
    public static final String NAME = "name";

    public static final Integer NEW_ORDER = 2;
    public static final String NEW_NAME = "new name";

    public static Level getBlankEntity() {
        return getBlankEntity(null);
    }

    public static Level getBlankEntityWithOrder(final Integer order) {
        final var level = getBlankEntity(null);
        level.setOrder(order);
        return level;
    }

    public static Level getBlankEntity(final Long id) {
        final var level = new Level();
        level.setId(id);
        level.setOrder(ORDER);
        level.setName(NAME);
        return level;
    }

    public static LevelDto getBlankDto() {
        return getBlankDto(null);
    }


    public static LevelDto getBlankDto(final Long id) {
        return new LevelDto(id, NAME, ORDER, Collections.emptyList());
    }

    public static LevelDto getBlankDto(final Long id, List<TaskDto> tasks) {
        return new LevelDto(id, NAME, ORDER, tasks);
    }

    public static void updateEntity(final Level level) {
        level.setOrder(NEW_ORDER);
        level.setName(NEW_NAME);
    }

    public static void checkUpdated(final Level level) {
        assertThat(level.getOrder()).isEqualTo(NEW_ORDER);
        assertThat(level.getName()).isEqualTo(NEW_NAME);
    }

    public static void checkEntity(
            final Level actual,
            final Level expected,
            final boolean checkId
    ) {
        if (checkId) {
            assertThat(actual.getId()).isEqualTo(expected.getId());
        }
        assertThat(actual.getOrder()).isEqualTo(expected.getOrder());
        assertThat(actual.getName()).isEqualTo(expected.getName());
    }

}
