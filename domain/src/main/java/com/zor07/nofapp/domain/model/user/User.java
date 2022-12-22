package com.zor07.nofapp.domain.model.user;

import java.util.Collection;

public record User(
        Long id,
        String name,
        String username,
        String password,
        Collection<Role> roles
) {
}
