package com.zor07.nofapp.model.notes;

import com.zor07.nofapp.model.user.UserModel;


public record NotebookModel(
        Long id,
        UserModel user,
        String name,
        String description
) {

}
