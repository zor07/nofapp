package com.zor07.nofapp.test;

import com.zor07.nofapp.entity.user.Role;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.security.UserRole;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTestUtils {

    public static final String DEFAULT_ROLE = "ROLE_USER";
    public static final String DEFAULT_USERNAME = "user";
    public static final String DEFAULT_PASSWORD = "pass";

    public static Role createRole() {
        return new Role(null, DEFAULT_ROLE);
    }

    public static Role createAdminRole() {
        return new Role(null, UserRole.ROLE_ADMIN.getRoleName());
    }

    public static User createUser() {
        return createUser(DEFAULT_USERNAME);
    }

    public static User createUser(final String name) {
        return new User(null, name, name, DEFAULT_PASSWORD, new ArrayList<>());
    }

    public static void checkEntity(
            final User actual,
            final User expected,
            final boolean checkId
    ) {
        if (checkId) {
            assertThat(actual.getId()).isEqualTo(expected.getId());
        }
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
        assertThat(actual.getRoles()).allMatch(
                role -> role.getName().equals(DEFAULT_ROLE)
        );
    }

}
