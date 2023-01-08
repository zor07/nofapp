package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zor07.nofapp.test.FileTestUtils;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskContentTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.mapstruct.factory.Mappers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskMapperTest {

    private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Long ID = 1L;

    @Test
    void toDto() throws JsonProcessingException {
        final var level = LevelTestUtils.getBlankEntity(ID);
        final var file = FileTestUtils.getBlankEntity();
        final var taskContent = TaskContentTestUtils.getBlankEntity(ID, file);
        final var entity = TaskTestUtils.getBlankEntity(ID, taskContent, level);

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
    void toEntity() throws IOException {
        final var taskContent = TaskContentTestUtils.getBlankDto(ID);
        final var level = LevelTestUtils.getBlankDto(ID);
        final var dto = TaskTestUtils.getBlankDto(ID, taskContent, level);

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
