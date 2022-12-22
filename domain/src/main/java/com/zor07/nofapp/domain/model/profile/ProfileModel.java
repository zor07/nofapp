package com.zor07.nofapp.domain.model.profile;

import com.zor07.nofapp.domain.model.file.FileModel;
import com.zor07.nofapp.domain.model.user.UserModel;

import java.time.Instant;

public record ProfileModel(
        Long id,
        UserModel user,
        Instant timerStart,
        FileModel avatar
) {
}