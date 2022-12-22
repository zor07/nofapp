package com.zor07.nofapp.domain.model.timer;

import com.zor07.nofapp.domain.model.user.User;

import java.time.Instant;

public record Timer(
        Long id,
        User user,
        Instant start,
        Instant stop,
        String description
) {

}
