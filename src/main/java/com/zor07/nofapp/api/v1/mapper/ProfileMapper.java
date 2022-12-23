package com.zor07.nofapp.api.v1.mapper;

import com.zor07.nofapp.api.v1.dto.profile.ProfileDto;
import com.zor07.nofapp.api.v1.dto.auth.UserInfoDto;
import com.zor07.nofapp.entity.profile.Profile;
import com.zor07.nofapp.entity.user.User;
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

    default UserInfoDto mapUser(final User user) {
        return new UserInfoDto(String.valueOf(user.getId()), user.getName(), user.getUsername());
    }

    default String getAvatarUri(final Profile profile) {
        final var avatar = profile.getAvatar();
        if (avatar == null) {
            return null;
        }
        return String.format("%s/%s", avatar.getBucket(), avatar.getKey());
    }

    @Mappings({
            @Mapping(target = "avatarUri", expression = "java(getAvatarUri(profile))"),
            @Mapping(target = "user", expression = "java(mapUser(profile.getUser()))")
    })
    ProfileDto toDto(final Profile profile, final @Context TimeZone timeZone);

}
