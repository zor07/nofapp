package com.zor07.nofapp.domain.model.practice;

import com.zor07.nofapp.domain.model.user.UserModel;

public record UserPracticeModel(
        UserPracticeKeyModel key,
        UserModel user,
        PracticeModel practice
) {

}
