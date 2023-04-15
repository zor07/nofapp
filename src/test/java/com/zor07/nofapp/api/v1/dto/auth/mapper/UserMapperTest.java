package com.zor07.nofapp.api.v1.dto.auth.mapper;

import com.zor07.nofapp.api.v1.dto.auth.UserRegisterDto;
import com.zor07.nofapp.entity.user.Role;
import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.security.UserRole;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RoleRepository roleRepository = new RoleRepository() {

        //region unused methods
        @Override
        public List<Role> findAll() {
            return null;
        }

        @Override
        public List<Role> findAll(Sort sort) {
            return null;
        }

        @Override
        public Page<Role> findAll(Pageable pageable) {
            return null;
        }

        @Override
        public List<Role> findAllById(Iterable<Long> iterable) {
            return null;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(Long aLong) {

        }

        @Override
        public void delete(Role role) {

        }

        @Override
        public void deleteAllById(Iterable<? extends Long> iterable) {

        }

        @Override
        public void deleteAll(Iterable<? extends Role> iterable) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public <S extends Role> S save(S s) {
            return null;
        }

        @Override
        public <S extends Role> List<S> saveAll(Iterable<S> iterable) {
            return null;
        }

        @Override
        public Optional<Role> findById(Long aLong) {
            return Optional.empty();
        }

        @Override
        public boolean existsById(Long aLong) {
            return false;
        }

        @Override
        public void flush() {

        }

        @Override
        public <S extends Role> S saveAndFlush(S s) {
            return null;
        }

        @Override
        public <S extends Role> List<S> saveAllAndFlush(Iterable<S> iterable) {
            return null;
        }

        @Override
        public void deleteAllInBatch(Iterable<Role> iterable) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<Long> iterable) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public Role getOne(Long aLong) {
            return null;
        }

        @Override
        public Role getById(Long aLong) {
            return null;
        }

        @Override
        public <S extends Role> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Role> List<S> findAll(Example<S> example) {
            return null;
        }

        @Override
        public <S extends Role> List<S> findAll(Example<S> example, Sort sort) {
            return null;
        }

        @Override
        public <S extends Role> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Role> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Role> boolean exists(Example<S> example) {
            return false;
        }
        //endregion unused methods

        @Override
        public Role findByName(String name) {
            return new Role(1L, UserRole.ROLE_USER.getRoleName());
        }
    };

    private final UserMapper userMapper = new UserMapper(passwordEncoder, roleRepository);

    @Test
    void toUserTest() {
        final var password = "password";
        final var username = "username";
        final var name = "name";
        final var dto = new UserRegisterDto(name,
                username,
                password);

        final var user = userMapper.toUser(dto);

        assertThat(user.getId()).isNull();
        assertThat(user.getPassword()).isNotNull().isNotEqualTo(password);
        assertThat(user.getName()).isNotNull().isEqualTo(name);
        assertThat(user.getUsername()).isNotNull().isEqualTo(username);
        assertThat(user.getRoles())
                .hasSize(1)
                .allMatch(r ->
                        r.getName().equals(UserRole.ROLE_USER.getRoleName()) &&
                                r.getId().equals(1L)
                );

    }

}
