package com.zor07.nofapp.domain.model.notes;

import com.zor07.nofapp.domain.model.user.User;


public record Notebook(
        Long id,
        User user,
        String name,
        String description
) {

}
