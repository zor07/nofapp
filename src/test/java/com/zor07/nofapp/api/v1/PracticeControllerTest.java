package com.zor07.nofapp.api.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zor07.nofapp.api.v1.dto.PracticeDto;
import com.zor07.nofapp.practice.Practice;
import com.zor07.nofapp.practice.PracticeRepository;
import com.zor07.nofapp.practice.PracticeTag;
import com.zor07.nofapp.practice.PracticeTagRepository;
import com.zor07.nofapp.practice.UserPractice;
import com.zor07.nofapp.practice.UserPracticeKey;
import com.zor07.nofapp.practice.UserPracticeRepository;
import com.zor07.nofapp.test.AbstractApiTest;
import com.zor07.nofapp.user.RoleRepository;
import com.zor07.nofapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PracticeControllerTest extends AbstractApiTest {

    private static final String TAG_NAME = "tag";
    private static final String PRACTICE_NAME = "practice";
    private static final String PRACTICE_DESC = "description";
    private static final String PRACTICE_DATA = "data";
    private static final String USER_1 = "user1";
    private static final String USER_2 = "user2";
    private static final String PRACTICE_ENDPOINT = "/api/v1/practice";
    private static final String IS_PUBLIC_PARAM = "isPublic";

    private @Autowired WebApplicationContext context;
    private @Autowired PracticeRepository practiceRepository;
    private @Autowired UserPracticeRepository userPracticeRepository;
    private @Autowired PracticeTagRepository tagRepository;
    private @Autowired UserRepository userRepository;
    private @Autowired RoleRepository roleRepository;

    private MockMvc mvc;

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
        createPracticeTag();
        userService.saveUser(createUser(USER_1));
        userService.saveUser(createUser(USER_2));
        userService.saveRole(createRole());
        userService.addRoleToUser(USER_1, DEFAULT_ROLE);
        userService.addRoleToUser(USER_2, DEFAULT_ROLE);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterClass
    void teardown() {
        clearDb();
    }

    @Test
    void getPublicPracticesTest() throws Exception {
        practiceRepository.save(createPractice(true));
        practiceRepository.save(createPractice(true));
        practiceRepository.save(createPractice(true));

        final var authHeader = getAuthHeader(mvc, USER_1);
        final var content1 = mvc.perform(get(PRACTICE_ENDPOINT)
                .param(IS_PUBLIC_PARAM, String.valueOf(true))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final var practices1 = objectMapper.readValue(content1, new TypeReference<List<PracticeDto>>(){});
        assertThat(practices1).hasSize(3);
        assertThat(practices1).allMatch(p ->
                p.isPublic &&
                p.practiceTag.name.equals(TAG_NAME) &&
                p.data.equals(PRACTICE_DATA) &&
                p.description.equals(PRACTICE_DESC) &&
                p.name.equals(PRACTICE_NAME));

        final var content2 = mvc.perform(get(PRACTICE_ENDPOINT)
                .param(IS_PUBLIC_PARAM, String.valueOf(false))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final var practices2 = objectMapper.readValue(content2, new TypeReference<List<PracticeDto>>(){});
        assertThat(practices2).isEmpty();
    }

    @Test
    void getUserPracticesTest() throws Exception {

        final var p1 = practiceRepository.save(createPractice(true));
        final var p2 = practiceRepository.save(createPractice(true));
        final var p3 = practiceRepository.save(createPractice(true));
        final var p4 = practiceRepository.save(createPractice(false));
        final var p5 = practiceRepository.save(createPractice(false));

        addPracticeToUser(p2, USER_1);
        addPracticeToUser(p3, USER_1);
        addPracticeToUser(p4, USER_1);
        addPracticeToUser(p1, USER_2);
        addPracticeToUser(p3, USER_2);
        addPracticeToUser(p5, USER_2);

        final var authHeader = getAuthHeader(mvc, USER_1);
        final var content = mvc.perform(get(PRACTICE_ENDPOINT)
                .param(IS_PUBLIC_PARAM, String.valueOf(true))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final var practices = objectMapper.readValue(content, new TypeReference<List<PracticeDto>>(){});
        assertThat(practices).hasSize(3);
    }


    private void createPracticeTag() {
        final var practiceTag = new PracticeTag();
        practiceTag.setName(TAG_NAME);
        tagRepository.save(practiceTag);
    }

    private void addPracticeToUser(final Practice practice, final String username) {
        final var user = userService.getUser(username);
        final var userPractice = new UserPractice(
                new UserPracticeKey(user.getId(), practice.getId()),
                user,
                practice
        );
        userPracticeRepository.save(userPractice);
    }


    private Practice createPractice(final boolean isPublic) {
        final var practice = new Practice();
        practice.setPracticeTag(tagRepository.findAll().get(0));
        practice.setName(PRACTICE_NAME);
        practice.setDescription(PRACTICE_DESC);
        practice.setData(PRACTICE_DATA);
        practice.setPublic(isPublic);

        return practice;
    }
}
