package com.zor07.nofapp.api.v1.mapper;

import com.zor07.nofapp.api.v1.dto.ProfileDto;
import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.entity.Profile;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    default LocalDateTime fromInstant(final Instant instant,
                                      final @Context TimeZone timeZone) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, timeZone.toZoneId());
    }

    @Mappings({
            @Mapping(target = "avatarUrl", expression = "java(p.getAvatar() == null ? null : s3Service.getResourceUrl(p.getAvatar().getBucket(), p.getAvatar().getPrefix(), p.getAvatar().getKey()))"),
            @Mapping(target = "userId", expression = "java(p.getUser().getId())")
    })
    ProfileDto toDto(final Profile p, final @Context TimeZone timeZone, final @Context S3Service s3Service);

}
