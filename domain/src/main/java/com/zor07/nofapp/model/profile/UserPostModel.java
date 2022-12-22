package com.zor07.nofapp.model.profile;

import com.zor07.nofapp.model.notes.NoteModel;
import com.zor07.nofapp.model.user.UserModel;

public record UserPostModel(
        UserPostKeyModel userPostKey,
        UserModel user,
        NoteModel note
) {
}
