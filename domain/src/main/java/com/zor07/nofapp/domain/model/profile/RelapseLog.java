package com.zor07.nofapp.domain.model.profile;

import com.zor07.nofapp.domain.model.user.User;

import java.time.Instant;


public record RelapseLog(
        Long id,
        User user,
        Instant start,
        Instant stop
) {

}
