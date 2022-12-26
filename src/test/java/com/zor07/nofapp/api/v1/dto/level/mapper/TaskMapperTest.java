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

        final var dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(entity.getId());
        assertThat(dto.name()).isEqualTo(entity.getName());
        assertThat(dto.order()).isEqualTo(entity.getOrder());
        TaskContentMapperTest.checkDto(dto.taskContent(), entity.getTaskContent());
        LevelMapperTest.check(entity.getLevel(), dto.level());
    }

    @Test
    void toEntity() throws IOException {
        final var taskContent = TaskContentTestUtils.getBlankDto(ID);
        final var level = LevelTestUtils.getBlankDto(ID);
        final var dto = TaskTestUtils.getBlankDto(ID, taskContent, level);

        final var entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(dto.id());
        assertThat(entity.getName()).isEqualTo(dto.name());
        assertThat(entity.getDescription()).isEqualTo(dto.description());
        LevelMapperTest.check(entity.getLevel(), dto.level());
        assertThat(entity.getTaskContent().getId()).isEqualTo(dto.taskContent().id());
        assertThat(entity.getTaskContent().getTitle()).isEqualTo(dto.taskContent().title());
        assertThat(
                objectMapper.readTree(entity.getTaskContent().getData())
        ).isEqualTo(
                objectMapper.readTree(dto.taskContent().data().toString())
        );
        assertThat(entity.getTaskContent().getFile()).isNull();
    }
}