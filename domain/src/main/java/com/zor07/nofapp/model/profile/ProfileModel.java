package com.zor07.nofapp.model.profile;

import com.zor07.nofapp.model.file.FileModel;
import com.zor07.nofapp.model.user.UserModel;

import java.time.Instant;

public record ProfileModel(
        Long id,
        UserModel user,
        Instant timerStart,
        FileModel avatar
) {
}