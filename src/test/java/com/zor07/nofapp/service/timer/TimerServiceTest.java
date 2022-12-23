package com.zor07.nofapp.service.timer;


import com.zor07.nofapp.entity.user.Role;
import com.zor07.nofapp.entity.timer.Timer;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.repository.user.RoleRepository;
import com.zor07.nofapp.repository.timer.TimerRepository;
import com.zor07.nofapp.repository.user.UserRepository;
import com.zor07.nofapp.security.UserRole;
import com.zor07.nofapp.service.timer.TimerService;
import com.zor07.nofapp.service.user.UserService;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class TimerServiceTest extends AbstractApplicationTest {

    private static final String DEFAULT_PASSWORD = "pass";
    private static final String USER_1 = "user1";
    private static final String USER_2 = "user2";

    private static final Instant TIMER_START = Instant.parse("2022-05-01T15:26:00Z");
    private static final Instant TIMER_STOP = Instant.parse("2022-05-01T15:27:00Z");
    private static final String TIMER_DESCRIPTION = "timer desc";

    private @Autowired TimerService timerService;
    private @Autowired TimerRepository timerRepository;
    private @Autowired UserService userService;
    private @Autowired UserRepository userRepository;
    private @Autowired RoleRepository roleRepository;

    private User getUser(final String username) {
        return userService.getUser(username);
    }
    private User persistUser(final String name) {
        return userService.saveUser(new User(null, name, name, DEFAULT_PASSWORD, new ArrayList<>()));
    }

    private Timer createTimer(final User user) {
        final var timer = new Timer();
        timer.setStart(TIMER_START);
        timer.setStop(TIMER_STOP);
        timer.setUser(user);
        timer.setDescription(TIMER_DESCRIPTION);
        return timer;
    }

    private Timer persistTimer(final Timer timer) {
        return timerRepository.save(timer);
    }

    private Role createRole() {
        return userService.saveRole(new Role(null, UserRole.ROLE_USER.getRoleName()));
    }

    private void clearDb() {
        timerRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @BeforeMethod
    public void setup() {
        clearDb();
        userService.saveUser(persistUser(USER_1));
        userService.saveUser(persistUser(USER_2));
        userService.saveRole(createRole());
        userService.addRoleToUser(USER_1, UserRole.ROLE_USER.getRoleName());
        userService.addRoleToUser(USER_2, UserRole.ROLE_USER.getRoleName());
    }

    @AfterClass
    void teardown() {
        clearDb();
    }

    @Test
    void findAllByUserId_shouldFindUserTimers() {
        //given
        final var user = getUser(USER_1);
        persistTimer(createTimer(user));
        persistTimer(createTimer(user));
        persistTimer(createTimer(user));

        //when
        final var all = timerService.findAllByUserId(user.getId());

        //then
        assertThat(all).hasSize(3);
        all.forEach(timer -> {
            assertThat(timer.getId()).isNotNull();
            assertThat(timer.getUser().getName()).isEqualTo(USER_1);
            assertThat(timer.getStart()).isEqualTo(TIMER_START);
            assertThat(timer.getStop()).isEqualTo(TIMER_STOP);
            assertThat(timer.getDescription()).isEqualTo(TIMER_DESCRIPTION);
        });
    }

    @Test
    void save_shouldSaveTimer() {
        //given
        final var user = getUser(USER_1);
        final var timer = createTimer(user);

        //when
        timerService.save(timer);

        //then
        final var all = timerRepository.findAll();
        final var actual = all.get(0);
        assertThat(all).hasSize(1);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getUser().getName()).isEqualTo(USER_1);
        assertThat(actual.getStart()).isEqualTo(TIMER_START);
        assertThat(actual.getStop()).isEqualTo(TIMER_STOP);
        assertThat(actual.getDescription()).isEqualTo(TIMER_DESCRIPTION);
    }

    @Test
    void stopTimer_shouldStopTimer() {
        //given
        final var user = getUser(USER_1);
        final var timer = createTimer(user);
        timer.setStop(null);
        persistTimer(timer);
        assertThat(timerRepository.findAll().get(0).getStop()).isNull();

        //when
        timerService.stopTimer(timer.getId(), user.getId());

        //then
        final var all = timerRepository.findAll();
        final var actual = all.get(0);
        assertThat(all).hasSize(1);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getUser().getName()).isEqualTo(USER_1);
        assertThat(actual.getStart()).isEqualTo(TIMER_START);
        assertThat(actual.getStop()).isCloseTo(Instant.now(), within(3, ChronoUnit.SECONDS));
        assertThat(actual.getDescription()).isEqualTo(TIMER_DESCRIPTION);
    }

    @Test
    void stopTimer_shouldNotStopTimerIfUserIsWrong() {
        //given
        final var user1 = getUser(USER_1);
        final var user2 = getUser(USER_2);
        final var timer = createTimer(user1);
        timer.setStop(null);
        persistTimer(timer);
        assertThat(timerRepository.findAll().get(0).getStop()).isNull();

        //when
        timerService.stopTimer(timer.getId(), user2.getId());

        //then
        final var all = timerRepository.findAll();
        final var actual = all.get(0);
        assertThat(all).hasSize(1);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getUser().getName()).isEqualTo(USER_1);
        assertThat(actual.getStart()).isEqualTo(TIMER_START);
        assertThat(actual.getStop()).isNull();
        assertThat(actual.getDescription()).isEqualTo(TIMER_DESCRIPTION);
    }

    @Test
    void deleteByIdAndUserId_shouldDeleteTimer() {
        //given
        final var user = getUser(USER_1);
        final var timer = persistTimer(createTimer(user));

        //when
        timerService.deleteByIdAndUserId(timer.getId(), user.getId());

        //then
        assertThat(timerRepository.findAll()).isEmpty();
    }

}
