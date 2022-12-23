package com.zor07.nofapp.api.v1.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.api.v1.dto.practice.PracticeDto;
import com.zor07.nofapp.api.v1.dto.practice.PracticeTagDto;
import com.zor07.nofapp.api.v1.dto.practice.mapper.PracticeMapper;
import com.zor07.nofapp.entity.practice.Practice;
import com.zor07.nofapp.entity.practice.PracticeTag;
import org.mapstruct.factory.Mappers;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PracticeMapperTest {
    private static final Long ID = 111L;
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String DATA = "{\"data\": \"data\"}";


    private final PracticeMapper mapper = Mappers.getMapper(PracticeMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldMapDtoToEntity() throws Exception {
        //given
        final var tagDto = new PracticeTagDto(ID, NAME);
        final var practiceDto = new PracticeDto(
                ID,
                tagDto,
                NAME,
                DESCRIPTION,
                objectMapper.readTree(DATA),
                true
        );

        //when
        final var practiceEntity = mapper.toEntity(practiceDto);

        assertThat(practiceEntity.getId()).isEqualTo(ID);
        assertThat(practiceEntity.getPracticeTag().getId()).isEqualTo(ID);
        assertThat(practiceEntity.getPracticeTag().getName()).isEqualTo(NAME);
        assertThat(practiceEntity.getName()).isEqualTo(NAME);
        assertThat(practiceEntity.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(practiceEntity.getData()).isEqualTo(objectMapper.readTree(DATA).toString());
        assertThat(practiceEntity.isPublic()).isTrue();
    }

    @Test
    void shouldMapEntityToDto() throws Exception {
        //given
        final var tag = new PracticeTag();
        tag.setId(ID);
        tag.setName(NAME);
        final var practice = new Practice();
        practice.setId(ID);
        practice.setPracticeTag(tag);
        practice.setName(NAME);
        practice.setDescription(DESCRIPTION);
        practice.setData(DATA);
        practice.setPublic(false);

        //when
        final var dto = mapper.toDto(practice);
        assertThat(dto.id()).isEqualTo(ID);
        assertThat(dto.practiceTag().id()).isEqualTo(ID);
        assertThat(dto.practiceTag().name()).isEqualTo(NAME);
        assertThat(dto.name()).isEqualTo(NAME);
        assertThat(dto.description()).isEqualTo(DESCRIPTION);
        assertThat(dto.data()).isEqualTo(objectMapper.readTree(DATA));
        assertThat(dto.isPublic()).isFalse();
    }


}
