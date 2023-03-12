package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.test.FileTestUtils;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.mapstruct.factory.Mappers;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskMapperTest {

    private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Long ID = 1L;

    @Test
    void toDto() throws JsonProcessingException {
        final var file = FileTestUtils.getBlankEntity();
        final var level = LevelTestUtils.getBlankEntity(ID);
        final var entity = TaskTestUtils.getBlankEntity(ID, level, file);

        final var taskDto = mapper.toDto(entity);

        assertThat(taskDto.id()).isEqualTo(entity.getId());
        assertThat(taskDto.name()).isEqualTo(entity.getName());
        assertThat(taskDto.order()).isEqualTo(entity.getOrder());
        assertThat(taskDto.fileUri()).isEqualTo(String.format("%s/%s", entity.getFile().getBucket(), entity.getFile().getKey()));
        assertThat(taskDto.data()).isEqualTo(OBJECT_MAPPER.readTree(entity.getData()));

        final var levelDto = taskDto.level();
        assertThat(levelDto.id()).isEqualTo(ID);
        assertThat(levelDto.name()).isEqualTo(level.getName());
        assertThat(levelDto.order()).isEqualTo(level.getOrder());
    }

    @Test
    void toEntity() throws JsonProcessingException {
        final var levelDto = LevelTestUtils.getBlankDto(ID);
        final var taskDto = TaskTestUtils.getBlankDto(ID, levelDto);

        final var task = mapper.toEntity(taskDto);

        assertThat(task.getId()).isEqualTo(taskDto.id());
        assertThat(task.getName()).isEqualTo(taskDto.name());
        assertThat(task.getDescription()).isEqualTo(taskDto.description());
        assertThat(OBJECT_MAPPER.readTree(task.getData())).isEqualTo(taskDto.data());
        assertThat(task.getFile()).isNull();

        final var level = task.getLevel();
        assertThat(level.getId()).isEqualTo(ID);
        assertThat(level.getName()).isEqualTo(levelDto.name());
        assertThat(level.getOrder()).isEqualTo(levelDto.order());
    }
}
