package com.zor07.nofapp.domain.model.profile;

import com.zor07.nofapp.domain.model.user.UserModel;

import java.time.Instant;


public record RelapseLogModel(
        Long id,
        UserModel user,
        Instant start,
        Instant stop
) {

}
