package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.mapstruct.factory.Mappers;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskMapperTest {

    private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);
    private static final Long ID = 1L;

    @Test
    void toDto() {
        final var level = LevelTestUtils.getBlankEntity(ID);
        final var entity = TaskTestUtils.getBlankEntity(ID, level);

        final var taskDto = mapper.toDto(entity);

        assertThat(taskDto.id()).isEqualTo(entity.getId());
        assertThat(taskDto.name()).isEqualTo(entity.getName());
        assertThat(taskDto.order()).isEqualTo(entity.getOrder());

        final var levelDto = taskDto.level();
        assertThat(levelDto.id()).isEqualTo(ID);
        assertThat(levelDto.name()).isEqualTo(level.getName());
        assertThat(levelDto.order()).isEqualTo(level.getOrder());
    }

    @Test
    void toEntity() {
        final var levelDto = LevelTestUtils.getBlankDto(ID);
        final var taskDto = TaskTestUtils.getBlankDto(ID, levelDto);

        final var task = mapper.toEntity(taskDto);

        assertThat(task.getId()).isEqualTo(taskDto.id());
        assertThat(task.getName()).isEqualTo(taskDto.name());
        assertThat(task.getDescription()).isEqualTo(taskDto.description());

        final var level = task.getLevel();
        assertThat(level.getId()).isEqualTo(ID);
        assertThat(level.getName()).isEqualTo(levelDto.name());
        assertThat(level.getOrder()).isEqualTo(levelDto.order());
    }
}
