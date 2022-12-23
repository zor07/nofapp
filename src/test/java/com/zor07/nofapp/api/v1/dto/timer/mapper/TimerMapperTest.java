package com.zor07.nofapp.api.v1.dto.timer.mapper;

import com.zor07.nofapp.api.v1.dto.timer.TimerDto;
import com.zor07.nofapp.entity.timer.Timer;
import com.zor07.nofapp.entity.user.User;
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

public class TimerMapperTest {

    private final TimerMapper timerMapper = Mappers.getMapper(TimerMapper.class);

    private static final Long ID = 111L;
    private static final String DESCRIPTION = "Description";
    private static final String USERNAME = "user";
    private static final String PASS = "pass";
    private static final Instant START_INSTANT = Instant.parse("2022-05-01T15:26:00Z");
    private static final Instant STOP_INSTANT = Instant.parse("2022-05-01T15:27:00Z");
    private static final LocalDateTime START_LOCAL_DATE_TIME = LocalDateTime.of(2022, 5, 1, 18, 26);
    private static final LocalDateTime STOP_LOCAL_DATE_TIME = LocalDateTime.of(2022, 5, 1, 18, 27);
    private static final User USER = new User(null, USERNAME, USERNAME, PASS, Collections.emptyList());

    @Test
    void shouldMapEntityToDtoWhenStopIsNotNull() {
        //given
        final var entity = new Timer();
        entity.setId(ID);
        entity.setDescription(DESCRIPTION);
        entity.setStart(START_INSTANT);
        entity.setStop(STOP_INSTANT);
        entity.setUser(USER);

        //when
        final var timerDto = timerMapper.toDto(entity, TimeZone.getTimeZone(ZoneId.systemDefault()));

        //then
        assertThat(timerDto.id()).isEqualTo(entity.getId());
        assertThat(timerDto.description()).isEqualTo(entity.getDescription());
        assertThat(timerDto.start()).isCloseTo(START_LOCAL_DATE_TIME, within(1, ChronoUnit.SECONDS));
        assertThat(timerDto.stop()).isCloseTo(STOP_LOCAL_DATE_TIME, within(1, ChronoUnit.SECONDS));
        assertThat(timerDto.isRunning()).isFalse();
    }

    @Test
    void shouldMapEntityToDtoWhenStopIsNull() {
        //given
        final var entity = new Timer();
        entity.setDescription(DESCRIPTION);
        entity.setId(ID);
        entity.setStart(START_INSTANT);
        entity.setUser(USER);

        //when
        final var timerDto = timerMapper.toDto(entity, TimeZone.getTimeZone(ZoneId.systemDefault()));

        //then
        assertThat(timerDto.id()).isEqualTo(entity.getId());
        assertThat(timerDto.description()).isEqualTo(entity.getDescription());
        assertThat(timerDto.start()).isCloseTo(START_LOCAL_DATE_TIME, within(1, ChronoUnit.SECONDS));
        assertThat(timerDto.stop()).isNull();
        assertThat(timerDto.isRunning()).isTrue();
    }

    @Test
    void shouldMapDtoToEntityWhenStopIsNull() {
        //given
        final var isRunning = false;
        final var dto = new TimerDto(ID, isRunning, START_LOCAL_DATE_TIME, null, DESCRIPTION);

        //when
        final var entity = timerMapper.toEntity(dto, TimeZone.getTimeZone(ZoneId.systemDefault()), USER);

        //then
        assertThat(entity.getId()).isEqualTo(ID);
        assertThat(entity.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(entity.getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(entity.getStart()).isCloseTo(START_INSTANT, within(1, ChronoUnit.SECONDS));
        assertThat(entity.getStop()).isNull();
    }

    @Test
    void shouldMapDtoToEntityWhenStopIsNotNull() {
        //given
        final var isRunning = true;
        final var dto = new TimerDto(ID, isRunning, START_LOCAL_DATE_TIME, STOP_LOCAL_DATE_TIME, DESCRIPTION);

        //when
        final var entity = timerMapper.toEntity(dto, TimeZone.getTimeZone(ZoneId.systemDefault()), USER);

        //then
        assertThat(entity.getId()).isEqualTo(ID);
        assertThat(entity.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(entity.getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(entity.getStart()).isCloseTo(START_INSTANT, within(1, ChronoUnit.SECONDS));
        assertThat(entity.getStop()).isCloseTo(STOP_INSTANT, within(1, ChronoUnit.SECONDS));
    }


}
