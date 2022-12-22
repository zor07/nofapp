package com.zor07.nofapp.model.practice;

import com.zor07.nofapp.model.user.UserModel;

public record UserPracticeModel(
        UserPracticeKeyModel key,
        UserModel user,
        PracticeModel practice
) {

}
