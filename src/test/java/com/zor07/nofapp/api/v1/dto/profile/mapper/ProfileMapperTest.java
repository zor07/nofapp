package com.zor07.nofapp.api.v1.dto.profile.mapper;

import com.zor07.nofapp.entity.file.File;
import com.zor07.nofapp.entity.profile.Profile;
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

public class ProfileMapperTest {

    private static final String USERNAME = "user";
    private static final String PASS = "pass";
    private static final Instant START_INSTANT = Instant.parse("2022-05-01T15:26:00Z");
    private static final LocalDateTime START_LOCAL_DATE_TIME = LocalDateTime.of(2022, 5, 1, 18, 26);
    private static final User USER = new User(null, USERNAME, USERNAME, PASS, Collections.emptyList());

    private static final String BUCKET = "profile-mapping-test";
    private static final String PREFIX = "prefix";
    private static final String KEY = "Key";

    private final ProfileMapper profileMapper = Mappers.getMapper(ProfileMapper.class);
    @Test
    void shouldMapEntityToDtoWhenPrefixIsNotNull() {
        //given
        final var entity = new Profile();
        entity.setUser(USER);
        entity.setTimerStart(START_INSTANT);
        entity.setAvatar(new File(null, BUCKET, PREFIX, KEY, "mime", null));
        //when
        final var profileDto = profileMapper.toDto(entity, TimeZone.getTimeZone(ZoneId.systemDefault()));

        //then
        assertThat(profileDto.id()).isEqualTo(entity.getId());
        assertThat(profileDto.user().id()).isEqualTo(String.valueOf(entity.getUser().getId()));
        assertThat(profileDto.user().name()).isEqualTo(entity.getUser().getName());
        assertThat(profileDto.user().username()).isEqualTo(entity.getUser().getUsername());
        assertThat(profileDto.timerStart()).isCloseTo(START_LOCAL_DATE_TIME, within(1, ChronoUnit.SECONDS));
        assertThat(profileDto.avatarUri()).isEqualTo(String.format("%s/%s", BUCKET, KEY));
    }

    @Test
    void shouldMapEntityToDtoWhenPrefixIsNull() {
        //given
        final var entity = new Profile();
        entity.setUser(USER);
        entity.setTimerStart(START_INSTANT);
        entity.setAvatar(new File(null, BUCKET, null, KEY, "mime", null));
        //when
        final var profileDto = profileMapper.toDto(entity, TimeZone.getTimeZone(ZoneId.systemDefault()));

        //then
        assertThat(profileDto.id()).isEqualTo(entity.getId());
        assertThat(profileDto.user().id()).isEqualTo(String.valueOf(entity.getUser().getId()));
        assertThat(profileDto.user().name()).isEqualTo(entity.getUser().getName());
        assertThat(profileDto.user().username()).isEqualTo(entity.getUser().getUsername());
        assertThat(profileDto.timerStart()).isCloseTo(START_LOCAL_DATE_TIME, within(1, ChronoUnit.SECONDS));
        assertThat(profileDto.avatarUri()).isEqualTo(String.format("%s/%s", BUCKET, KEY));
    }

    @Test
    void shouldMapEntityToDtoWhenAvatarIsNull() {
        //given
        final var entity = new Profile();
        entity.setUser(USER);
        entity.setTimerStart(START_INSTANT);
        //when
        final var profileDto = profileMapper.toDto(entity, TimeZone.getTimeZone(ZoneId.systemDefault()));

        //then
        assertThat(profileDto.id()).isEqualTo(entity.getId());
        assertThat(profileDto.timerStart()).isCloseTo(START_LOCAL_DATE_TIME, within(1, ChronoUnit.SECONDS));
        assertThat(profileDto.avatarUri()).isNull();
    }


}
