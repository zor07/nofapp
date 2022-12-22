package com.zor07.nofapp.domain.model.profile;

import com.zor07.nofapp.domain.model.notes.NoteModel;
import com.zor07.nofapp.domain.model.user.UserModel;

public record UserPostModel(
        UserPostKeyModel userPostKey,
        UserModel user,
        NoteModel note
) {
}
