package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.zor07.nofapp.api.v1.dto.level.LevelDto;
import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class LevelMapperTest  {

    private final TaskMapper taskMapper = new TaskMapper();
    private final LevelMapper mapper = new LevelMapper(taskMapper);

    private static final Long ID = 1L;

    static void check(final Level entity, final LevelDto dto) {
        assertThat(dto.id()).isEqualTo(entity.getId());
        assertThat(dto.name()).isEqualTo(entity.getName());
        assertThat(dto.order()).isEqualTo(entity.getOrder());
    }

    @Test
    void toDtoTest() {
        final var level = LevelTestUtils.getBlankEntity(ID);
        final var task1 = TaskTestUtils.getBlankEntity(level);
        final var task2 = TaskTestUtils.getBlankEntity(level);
        final var task3 = TaskTestUtils.getBlankEntity(level);
        final var tasks = Arrays.asList(task1, task2, task3);
        level.setTasks(tasks);

        final var dto = mapper.toDto(level);
        check(level, dto);
        assertThat(dto.tasks()).hasSize(3);
    }

    @Test
    void toEntityTest() {
        final var dto = LevelTestUtils.getBlankDto(ID);
        final var entity = mapper.toEntity(dto);
        check(entity, dto);
        assertThat(entity.getTasks()).isEmpty();
    }


}
