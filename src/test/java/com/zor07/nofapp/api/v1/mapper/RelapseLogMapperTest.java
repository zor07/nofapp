package com.zor07.nofapp.api.v1.mapper;

import com.zor07.nofapp.entity.profile.RelapseLog;
import com.zor07.nofapp.entity.User;
import org.mapstruct.factory.Mappers;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class RelapseLogMapperTest {

    private final RelapseLogMapper relapseLogMapper = Mappers.getMapper(RelapseLogMapper.class);

    private static final Long ID = 111L;
    private static final String USERNAME = "user";
    private static final String PASS = "pass";
    private static final Instant START_INSTANT = Instant.parse("2022-05-01T15:26:00Z");
    private static final Instant STOP_INSTANT = Instant.parse("2022-05-01T15:27:00Z");
    private static final LocalDateTime START_LOCAL_DATE_TIME = LocalDateTime.of(2022, 5, 1, 18, 26);
    private static final LocalDateTime STOP_LOCAL_DATE_TIME = LocalDateTime.of(2022, 5, 1, 18, 27);
    private static final User USER = new User(null, USERNAME, USERNAME, PASS, Collections.emptyList());


    @Test
    void shouldMapToDto() {
        //given
        final var entity = new RelapseLog();
        entity.setId(ID);
        entity.setStart(START_INSTANT);
        entity.setStop(STOP_INSTANT);
        entity.setUser(USER);

        //when
        final var dto = relapseLogMapper.toDto(entity, TimeZone.getTimeZone(ZoneId.systemDefault()));

        //then
        assertThat(dto.id()).isEqualTo(entity.getId());
        assertThat(dto.start()).isCloseTo(START_LOCAL_DATE_TIME, within(1, ChronoUnit.SECONDS));
        assertThat(dto.stop()).isCloseTo(STOP_LOCAL_DATE_TIME, within(1, ChronoUnit.SECONDS));
    }
}
