package com.zor07.nofapp.domain.model.practice;

import com.zor07.nofapp.domain.model.user.User;

public record UserPractice(
        UserPracticeKey key,
        User user,
        Practice practice
) {

}
