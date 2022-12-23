package com.zor07.nofapp.api.v1.dto.timer.mapper;

import com.zor07.nofapp.api.v1.dto.timer.TimerDto;
import com.zor07.nofapp.entity.timer.Timer;
import com.zor07.nofapp.entity.user.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Mapper(componentModel = "spring")
public interface TimerMapper {

    default LocalDateTime fromInstant(final Instant instant,
                                      final @Context TimeZone timeZone) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, timeZone.toZoneId());
    }

    default Instant fromLocalDateTime(final LocalDateTime localDateTime,
                                      final @Context TimeZone timeZone) {
        return localDateTime == null ? null : localDateTime.atZone(timeZone.toZoneId()).toInstant();
    }

    @Mapping(target = "isRunning", expression = "java(entity.getStop() == null)")
    TimerDto toDto(final Timer entity, final @Context TimeZone timeZone);
    @Mapping(target = "user", expression = "java(user)")
    Timer toEntity(final TimerDto dto, final @Context TimeZone timeZone, final @Context User user);

}
