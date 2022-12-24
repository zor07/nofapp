package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.test.FileTestUtils;
import com.zor07.nofapp.test.TaskContentTestUtils;
import org.mapstruct.factory.Mappers;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskContentMapperTest {

    private final TaskContentMapper mapper = Mappers.getMapper(TaskContentMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Long ID = 1L;

    @Test
    void toDtoTest() throws JsonProcessingException {
        final var file = FileTestUtils.getBlankEntity();
        final var entity = TaskContentTestUtils.getBlankEntity(ID, file);

        final var dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(entity.getId());
        assertThat(dto.title()).isEqualTo(entity.getTitle());
        assertThat(dto.fileUri()).isEqualTo(String.format("%s/%s", entity.getFile().getBucket(), entity.getFile().getKey()));
        assertThat(dto.data()).isEqualTo(objectMapper.readTree(entity.getData()));
    }

    @Test
    void toEntityTest() throws IOException {
        final var dto = TaskContentTestUtils.getBlankDto(ID);
        final var entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(dto.id());
        assertThat(entity.getTitle()).isEqualTo(dto.title());
        assertThat(objectMapper.readTree(entity.getData())).isEqualTo(dto.data());
        assertThat(entity.getFile()).isNull();
    }

}
