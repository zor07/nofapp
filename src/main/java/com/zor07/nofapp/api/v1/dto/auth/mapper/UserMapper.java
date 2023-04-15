package com.zor07.nofapp.api.v1.dto.auth.mapper;

import com.zor07.nofapp.api.v1.dto.auth.UserRegisterDto;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.security.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserMapper(PasswordEncoder passwordEncoder,
                      RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User toUser(final UserRegisterDto dto) {
        final var role = roleRepository.findByName(UserRole.ROLE_USER.getRoleName());
        return new User(
            null,
            dto.name(),
            dto.username(),
            passwordEncoder.encode(dto.password()),
            Collections.singletonList(role)
        );
    }

}
