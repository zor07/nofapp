package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.zor07.nofapp.api.v1.dto.level.LevelDto;
import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.test.LevelTestUtils;
import org.mapstruct.factory.Mappers;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LevelMapperTest {

    private final LevelMapper mapper = Mappers.getMapper(LevelMapper.class);

    private static final Long ID = 1L;

    @Test
    void toDtoTest() {
        var entity = LevelTestUtils.getBlankEntity(ID);
        var dto = mapper.toDto(entity);
        check(entity, dto);
    }

    @Test
    void toEntityTest() {
        var dto = LevelTestUtils.getBlankDto(ID);
        var entity = mapper.toEntity(dto);
        check(entity, dto);
    }

    private void check(final Level entity, final LevelDto dto) {
        assertThat(dto.id()).isEqualTo(entity.getId());
        assertThat(dto.name()).isEqualTo(entity.getName());
        assertThat(dto.order()).isEqualTo(entity.getOrder());
    }
}
