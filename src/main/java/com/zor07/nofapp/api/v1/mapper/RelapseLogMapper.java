package com.zor07.nofapp.api.v1.mapper;

import com.zor07.nofapp.api.v1.dto.RelapseLogDto;
import com.zor07.nofapp.entity.RelapseLog;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Mapper(componentModel = "spring")
public interface RelapseLogMapper {

    default LocalDateTime fromInstant(final Instant instant,
                                      final @Context TimeZone timeZone) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, timeZone.toZoneId());
    }

    default Instant fromLocalDateTime(final LocalDateTime localDateTime,
                                      final @Context TimeZone timeZone) {
        return localDateTime == null ? null : localDateTime.atZone(timeZone.toZoneId()).toInstant();
    }


    RelapseLogDto toDto(final RelapseLog entity, final @Context TimeZone timeZone);
}
