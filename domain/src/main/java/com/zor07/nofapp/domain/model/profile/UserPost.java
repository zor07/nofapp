package com.zor07.nofapp.domain.model.profile;

import com.zor07.nofapp.domain.model.notes.Note;
import com.zor07.nofapp.domain.model.user.User;

public record UserPost(
        UserPostKey userPostKey,
        User user,
        Note note
) {
}
