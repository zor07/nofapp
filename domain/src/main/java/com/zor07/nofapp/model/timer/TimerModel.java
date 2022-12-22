package com.zor07.nofapp.model.timer;

import com.zor07.nofapp.model.user.UserModel;

import java.time.Instant;

public record TimerModel(
        Long id,
        UserModel user,
        Instant start,
        Instant stop,
        String description
) {

}
