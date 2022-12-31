package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.api.v1.dto.level.TaskContentDto;
import com.zor07.nofapp.entity.level.TaskContent;
import com.zor07.nofapp.test.FileTestUtils;
import com.zor07.nofapp.test.TaskContentTestUtils;
import org.mapstruct.factory.Mappers;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskContentMapperTest {

    private final TaskContentMapper mapper = Mappers.getMapper(TaskContentMapper.class);
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Long ID = 1L;

    static void checkDto(final TaskContentDto  dto, final TaskContent entity) throws JsonProcessingException {
        assertThat(dto.id()).isEqualTo(entity.getId());
        assertThat(dto.title()).isEqualTo(entity.getTitle());
        assertThat(dto.fileUri()).isEqualTo(String.format("%s/%s", entity.getFile().getBucket(), entity.getFile().getKey()));
        assertThat(dto.data()).isEqualTo(OBJECT_MAPPER.readTree(entity.getData()));
    }

    @Test
    void toDtoTest() throws JsonProcessingException {
        final var file = FileTestUtils.getBlankEntity();
        final var entity = TaskContentTestUtils.getBlankEntity(ID, file);

        final var dto = mapper.toDto(entity);

        checkDto(dto, entity);
    }

    @Test
    void toEntityTest() throws IOException {
        final var dto = TaskContentTestUtils.getBlankDto(ID);
        final var entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(dto.id());
        assertThat(entity.getTitle()).isEqualTo(dto.title());
        assertThat(OBJECT_MAPPER.readTree(entity.getData())).isEqualTo(dto.data());
        assertThat(entity.getFile()).isNull();
    }

}
