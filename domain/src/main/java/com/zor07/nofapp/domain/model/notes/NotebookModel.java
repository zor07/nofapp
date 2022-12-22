package com.zor07.nofapp.domain.model.notes;

import com.zor07.nofapp.domain.model.user.UserModel;


public record NotebookModel(
        Long id,
        UserModel user,
        String name,
        String description
) {

}
