package com.zor07.nofapp.api.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zor07.nofapp.api.v1.dto.RelapseLogDto;
import com.zor07.nofapp.entity.profile.RelapseLog;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.repository.RelapseLogRepository;
import com.zor07.nofapp.repository.RoleRepository;
import com.zor07.nofapp.repository.UserRepository;
import com.zor07.nofapp.spring.AbstractApiTest;
import com.zor07.nofapp.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.zor07.nofapp.test.UserTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RelapseLogControllerTest extends AbstractApiTest {

    private static final String RELAPSE_LOG_ENDPOINT = "/api/v1/profiles/{userId}/relapses";
    private static final Instant START_1 = Instant.parse("2022-05-01T15:26:00Z");
    private static final Instant STOP_1 = Instant.parse("2022-05-01T15:27:00Z");

    @Autowired
    private RelapseLogRepository relapseLogRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private RoleRepository roleRepository;
    private MockMvc mvc;


    @BeforeMethod
    @AfterClass
    void clearDb() {
        relapseLogRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private String persistRole() {
        final var role = createRole();
        roleRepository.save(role);
        return role.getName();
    }
    private User persistUser(final String roleName) {
        final var user = createUser();
        userService.saveUser(user);
        userService.addRoleToUser(user.getUsername(), roleName);
        return user;
    }

    @Test
    void findAllByUserIdTest() throws Exception {
        // given
        final var roleName = persistRole();
        final var user = persistUser(roleName);
        final var all = relapseLogRepository.findAll();
        assertThat(all).isEmpty();
        persistRelapseLog(createRelapseLog(user));
        persistRelapseLog(createRelapseLog(user));
        persistRelapseLog(createRelapseLog(user));
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);

        // when
        final var content = mvc.perform(get(RELAPSE_LOG_ENDPOINT, user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        final var logs = objectMapper.readValue(content, new TypeReference<List<RelapseLogDto>>() {});
        assertThat(logs).hasSize(3);
        assertThat(logs.get(0).start())
                .isCloseTo(START_1.atZone(DateUtils.SYSTEM_TIMEZONE.toZoneId()).toLocalDateTime(),
                        within(1, ChronoUnit.SECONDS));
        assertThat(logs.get(0).stop())
                .isCloseTo(STOP_1.atZone(DateUtils.SYSTEM_TIMEZONE.toZoneId()).toLocalDateTime(),
                        within(1, ChronoUnit.SECONDS));
    }

    @Test
    void deleteByIdAndUserIdTest() throws Exception {
        // given
        assertThat(relapseLogRepository.findAll()).isEmpty();
        final var roleName = persistRole();
        final var user = persistUser(roleName);
        persistRelapseLog(createRelapseLog(user));
        final var id = relapseLogRepository.findAll().get(0).getId();
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);

        // when
        mvc.perform(delete(RELAPSE_LOG_ENDPOINT + "/{relapseLogId}", user.getId(), id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();

        // then
        assertThat(relapseLogRepository.findAll()).isEmpty();

    }

    private RelapseLog createRelapseLog(final User user) {
        final var log = new RelapseLog();
        log.setUser(user);
        log.setStart(START_1);
        log.setStop(STOP_1);
        return log;
    }

    private void persistRelapseLog(final RelapseLog log) {
        relapseLogRepository.save(log);
    }
}
