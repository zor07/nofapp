package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.test.FileTestUtils;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskContentTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import org.mapstruct.factory.Mappers;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskContentMapperTest {

    private final TaskContentMapper mapper = Mappers.getMapper(TaskContentMapper.class);
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Long ID = 1L;

    @Test
    void toDtoTest() throws JsonProcessingException {
        final var file = FileTestUtils.getBlankEntity();
        final var level = LevelTestUtils.getBlankEntity(ID);
        final var task = TaskTestUtils.getBlankEntity(ID, level);
        final var entity = TaskContentTestUtils.getBlankEntity(ID, task, file);

        final var dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(entity.getId());
        assertThat(dto.title()).isEqualTo(entity.getTitle());
        assertThat(dto.fileUri()).isEqualTo(String.format("%s/%s", entity.getFile().getBucket(), entity.getFile().getKey()));
        assertThat(dto.data()).isEqualTo(OBJECT_MAPPER.readTree(entity.getData()));
        
        final var taskDto = dto.task();
        assertThat(taskDto.id()).isEqualTo(ID);
        assertThat(taskDto.name()).isEqualTo(task.getName());
        assertThat(taskDto.description()).isEqualTo(task.getDescription());
        assertThat(taskDto.order()).isEqualTo(task.getOrder());
        
        final var levelDto = taskDto.level();
        assertThat(levelDto.id()).isEqualTo(ID);
        assertThat(levelDto.name()).isEqualTo(level.getName());
        assertThat(levelDto.order()).isEqualTo(level.getOrder());
    }

    @Test
    void toEntityTest() throws IOException {
        final var levelDto = LevelTestUtils.getBlankDto(ID);
        final var taskDto = TaskTestUtils.getBlankDto(ID, levelDto);
        final var dto = TaskContentTestUtils.getBlankDto(ID, taskDto);
        final var entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(dto.id());
        assertThat(entity.getTitle()).isEqualTo(dto.title());
        assertThat(OBJECT_MAPPER.readTree(entity.getData())).isEqualTo(dto.data());
        assertThat(entity.getFile()).isNull();

        final var task = entity.getTask();
        assertThat(task.getId()).isEqualTo(ID);
        assertThat(task.getName()).isEqualTo(taskDto.name());
        assertThat(task.getDescription()).isEqualTo(taskDto.description());
        assertThat(task.getOrder()).isEqualTo(taskDto.order());

        final var level = task.getLevel();
        assertThat(level.getId()).isEqualTo(ID);
        assertThat(level.getName()).isEqualTo(levelDto.name());
        assertThat(level.getOrder()).isEqualTo(levelDto.order());
    }

}
