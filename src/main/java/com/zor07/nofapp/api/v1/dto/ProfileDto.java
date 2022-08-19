package com.zor07.nofapp.api.v1.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.zor07.nofapp.api.v1.dto.auth.UserInfoDto;

import java.time.LocalDateTime;

public record ProfileDto(
        Long id,
        UserInfoDto user,
        String avatarUri,
        @JsonSerialize(using = LocalDateTimeSerializer.class) LocalDateTime timerStart) {}
