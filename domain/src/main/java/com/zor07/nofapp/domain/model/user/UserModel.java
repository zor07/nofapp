package com.zor07.nofapp.domain.model.user;

import java.util.Collection;

public record UserModel(
        Long id,
        String name,
        String username,
        String password,
        Collection<RoleModel> roles
) {
}
