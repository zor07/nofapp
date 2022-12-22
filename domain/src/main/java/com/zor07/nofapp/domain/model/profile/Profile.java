package com.zor07.nofapp.domain.model.profile;

import com.zor07.nofapp.domain.model.file.File;
import com.zor07.nofapp.domain.model.user.User;

import java.time.Instant;

public record Profile(
        Long id,
        User user,
        Instant timerStart,
        File avatar
) {
}