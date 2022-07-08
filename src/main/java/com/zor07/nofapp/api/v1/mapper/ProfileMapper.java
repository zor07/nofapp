package com.zor07.nofapp.api.v1.mapper;

import com.zor07.nofapp.api.v1.dto.ProfileDto;
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

    default String getAvatarUri(final Profile profile) {
        final var avatar = profile.getAvatar();
        if (avatar == null) {
            return null;
        }
        final var key = avatar.getPrefix() == null
                ? avatar.getKey()
                : String.format("%s/%s", avatar.getPrefix(), avatar.getKey());
        return String.format("%s/%s", avatar.getBucket(), key);
    }

    @Mappings({
            @Mapping(target = "avatarUri", expression = "java(getAvatarUri(profile))"),
            @Mapping(target = "userId", expression = "java(profile.getUser().getId())")
    })
    ProfileDto toDto(final Profile profile, final @Context TimeZone timeZone);

}
