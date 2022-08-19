package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.Practice;
import com.zor07.nofapp.entity.PracticeTag;
import com.zor07.nofapp.entity.Role;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.entity.UserPractice;
import com.zor07.nofapp.entity.UserPracticeKey;
import com.zor07.nofapp.service.UserService;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class UserPracticeRepositoryTest extends AbstractApplicationTest {

    private static final String ROLE_NAME = "role";
    private static final String USER_NAME = "user";
    private static final String USER_PASS = "pass";
    private static final String TAG_NAME = "tag";
    private static final String PRACTICE_NAME = "practice";
    private static final String PRACTICE_DESCRIPTION = "description";
    private static final String PRACTICE_DATA = "{\"data\": \"value\"}";

    private @Autowired PracticeRepository practiceRepository;
    private @Autowired PracticeTagRepository tagRepository;
    private @Autowired RoleRepository roleRepository;
    private @Autowired UserRepository userRepository;
    private @Autowired UserService userService;
    private @Autowired UserPracticeRepository userPracticeRepository;

    private void clearDb() {
        userPracticeRepository.deleteAll();
        practiceRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @BeforeMethod
    void setup() {
        clearDb();
        userService.saveUser(new User(null, USER_NAME, USER_NAME, USER_PASS, new ArrayList<>()));
        userService.saveRole(new Role(null, ROLE_NAME));
        userService.addRoleToUser(USER_NAME, ROLE_NAME);
    }

    @Test
    void testCreateDelete() {
        final var user = getUser();
        final var tag = createTag();
        final var practice = createPractice(tag);

        final var savedUserPractice = createUserPractice(user, practice);
        final var userPracticeKey = new UserPracticeKey(savedUserPractice.getUser().getId(), savedUserPractice.getPractice().getId());

        final var inserted = userPracticeRepository.findById(userPracticeKey).get();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getPractice().getName()).isEqualTo("practice");
        assertThat(inserted.getUser().getName()).isEqualTo(USER_NAME);

        userPracticeRepository.delete(inserted);
        assertThat(userPracticeRepository.findAll()).isEmpty();
    }

    @Test
    void findAllByUserTest() {
        final var user = getUser();
        final var tag = createTag();
        final var practice1 = createPractice(tag);
        final var practice2 = createPractice(tag);
        final var practice3 = createPractice(tag);

        createUserPractice(user, practice1);
        createUserPractice(user, practice2);
        createUserPractice(user, practice3);

        assertThat(userPracticeRepository.findAllByUserId(user.getId())).hasSize(3);
    }

    private User getUser() {
        return userService.getUser(USER_NAME);
    }

    private UserPractice createUserPractice(final User user, final Practice practice) {
        final var userPractice = new UserPractice();
        userPractice.setUser(user);
        userPractice.setPractice(practice);
        return userPracticeRepository.save(userPractice);
    }

    private PracticeTag createTag() {
        final var practiceTag = new PracticeTag();
        practiceTag.setName(TAG_NAME);
        final var tagId = tagRepository.save(practiceTag).getId();
        return tagRepository.getById(tagId);
    }

    private Practice createPractice(final PracticeTag tag) {
        final var practice = new Practice();
        practice.setPracticeTag(tag);
        practice.setName(PRACTICE_NAME);
        practice.setDescription(PRACTICE_DESCRIPTION);
        practice.setData(PRACTICE_DATA);

        final var savedPracticeId = practiceRepository.save(practice).getId();
        return practiceRepository.findById(savedPracticeId).get();
    }
}
