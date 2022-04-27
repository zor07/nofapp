package com.zor07.nofapp.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zor07.nofapp.api.v1.dto.PracticeDto;
import com.zor07.nofapp.api.v1.dto.PracticeTagDto;
import com.zor07.nofapp.practice.Practice;
import com.zor07.nofapp.practice.PracticeRepository;
import com.zor07.nofapp.practice.PracticeTag;
import com.zor07.nofapp.practice.PracticeTagRepository;
import com.zor07.nofapp.practice.UserPractice;
import com.zor07.nofapp.practice.UserPracticeKey;
import com.zor07.nofapp.practice.UserPracticeRepository;
import com.zor07.nofapp.security.UserRole;
import com.zor07.nofapp.test.AbstractApiTest;
import com.zor07.nofapp.user.RoleRepository;
import com.zor07.nofapp.user.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PracticeControllerTest extends AbstractApiTest {

    private static final String TAG_NAME = "tag";
    private static final String PRACTICE_NAME = "practice";
    private static final String PRACTICE_DESC = "description";
    private static final String PRACTICE_DATA_JSON = "{\"data\":\"data\"}";

    private static final String USER_1 = "user1";
    private static final String USER_2 = "user2";
    private static final String USER_ADMIN = "admin";

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
        userService.saveUser(createUser(USER_ADMIN));
        userService.saveRole(createAdminRole());
        userService.saveRole(createRole());
        userService.addRoleToUser(USER_ADMIN, UserRole.ROLE_ADMIN.getRoleName());
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
    void getPublicPractices_isOk() throws Exception {
        //given
        practiceRepository.save(createPractice(true));
        practiceRepository.save(createPractice(true));
        practiceRepository.save(createPractice(true));
        final var authHeader = getAuthHeader(mvc, USER_1);

        // when
        final var content = mvc.perform(get(PRACTICE_ENDPOINT)
                .param(IS_PUBLIC_PARAM, String.valueOf(true))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        final var practices = objectMapper.readValue(content, new TypeReference<List<PracticeDto>>(){});
        assertThat(practices).hasSize(3);
        assertThat(practices).allMatch(p ->
                p.isPublic &&
                p.practiceTag.name.equals(TAG_NAME) &&
                p.data.toString().equals(PRACTICE_DATA_JSON) &&
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
    void getPublicPractice_shouldReturnPracticeTest() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(true));
        final var endpoint = String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString());

        //when
        final var content = mvc.perform(get(endpoint)
                .param(IS_PUBLIC_PARAM, String.valueOf(false))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,  getAuthHeader(mvc, USER_1)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        final var dto = objectMapper.readValue(content, PracticeDto.class);
        assertThat(dto.id).isNotNull();
        assertThat(dto.practiceTag.id).isNotNull();
        assertThat(dto.practiceTag.name).isEqualTo(TAG_NAME);
        assertThat(dto.name).isEqualTo(PRACTICE_NAME);
        assertThat(dto.description).isEqualTo(PRACTICE_DESC);
        assertThat(dto.data.toString()).isEqualTo(PRACTICE_DATA_JSON);
        assertThat(dto.isPublic).isTrue();
    }

    @Test
    void addPracticeToUser_shouldAddPracticeToUser() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(true));
        final var endpoint = String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString());

        //when
        mvc.perform(put(endpoint)
                .param(IS_PUBLIC_PARAM, String.valueOf(false))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,  getAuthHeader(mvc, USER_1)))
                .andExpect(status().isAccepted());

        //then
        final var userPractice = userPracticeRepository.findByUserAndPractice(getUser(USER_1), practice);
        assertThat(userPractice.getUser().getName()).isEqualTo(USER_1);
        assertThat(userPractice.getPractice().getId()).isEqualTo(practice.getId());
        assertThat(userPractice.getPractice().getPracticeTag().getName()).isEqualTo(TAG_NAME);
        assertThat(userPractice.getPractice().getName()).isEqualTo(PRACTICE_NAME);
    }

    @Test
    void addPracticeToUser_shouldReturnAcceptedWhenAlreadyAdded() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(true));
        addPracticeToUser(practice, USER_1);
        final var endpoint = String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString());

        //when
        final var result = mvc.perform(put(endpoint)
                .param(IS_PUBLIC_PARAM, String.valueOf(false))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_1)));

        //then
        result.andExpect(status().isAccepted());
    }

    @Test
    void addPracticeToUser_shouldReturnNotFoundWhenPracticeNotExists() throws Exception {
        //given
        final var endpoint = String.format("%s/%s", PRACTICE_ENDPOINT, "12");

        //when
        mvc.perform(put(endpoint)
                .param(IS_PUBLIC_PARAM, String.valueOf(false))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,  getAuthHeader(mvc, USER_1)))
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    void addPracticeToUser_shouldReturnBadRequestIfPracticeIsNotPublic() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(false));
        addPracticeToUser(practice, USER_2);
        final var endpoint = String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString());

        //when
        mvc.perform(put(endpoint)
                .param(IS_PUBLIC_PARAM, String.valueOf(false))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,  getAuthHeader(mvc, USER_1)))
        //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserPractice_shouldReturnPracticeTest() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(false));
        addPracticeToUser(practice, USER_1);
        final var endpoint = String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString());

        //when
        final var content = mvc.perform(get(endpoint)
                .param(IS_PUBLIC_PARAM, String.valueOf(false))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,  getAuthHeader(mvc, USER_1)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        final var dto = objectMapper.readValue(content, PracticeDto.class);
        assertThat(dto.id).isNotNull();
        assertThat(dto.practiceTag.id).isNotNull();
        assertThat(dto.practiceTag.name).isEqualTo(TAG_NAME);
        assertThat(dto.name).isEqualTo(PRACTICE_NAME);
        assertThat(dto.description).isEqualTo(PRACTICE_DESC);
        assertThat(dto.data.toString()).isEqualTo(PRACTICE_DATA_JSON);
        assertThat(dto.isPublic).isFalse();
    }

    @Test
    void getUserPractice_shouldReturnBadRequestWhenUserDoesntOwnPractice() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(false));
        addPracticeToUser(practice, USER_2);
        final var endpoint = String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString());

        //when
        mvc.perform(get(endpoint)
                .param(IS_PUBLIC_PARAM, String.valueOf(false))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,  getAuthHeader(mvc, USER_1)))
        //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserPractice_shouldReturnNotFoundWhenPracticeNotExists() throws Exception {
        //given
        final var endpoint = String.format("%s/%s", PRACTICE_ENDPOINT, "7777");
        //when
        final var result = mvc.perform(get(endpoint)
                .param(IS_PUBLIC_PARAM, String.valueOf(false))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_1)));
        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void getPrivatePractices_returnsEmptyList() throws Exception {
        //given
        practiceRepository.save(createPractice(true));
        practiceRepository.save(createPractice(true));
        practiceRepository.save(createPractice(true));
        final var authHeader = getAuthHeader(mvc, USER_1);
        // when
        final var content = mvc.perform(get(PRACTICE_ENDPOINT)
                .param(IS_PUBLIC_PARAM, String.valueOf(false))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        // then
        final var practices = objectMapper.readValue(content, new TypeReference<List<PracticeDto>>(){});
        assertThat(practices).isEmpty();
    }

    @Test
    void getUserPractices_isOk() throws Exception {
        //given
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

        //when
        final var content = mvc.perform(get(PRACTICE_ENDPOINT)
                .param(IS_PUBLIC_PARAM, String.valueOf(true))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        final var practices = objectMapper.readValue(content, new TypeReference<List<PracticeDto>>(){});
        assertThat(practices).hasSize(3);
    }

    @Test
    void savePublicPractice_isForbiddenForUser() throws Exception {
        // given
        final var dtoString = createPracticeDtoString(true);
        final var userAuthHeader = getAuthHeader(mvc, USER_1);

        //when
        mvc.perform(post(PRACTICE_ENDPOINT)
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, userAuthHeader))
        //then
                .andExpect(status().isForbidden());
    }

    @Test
    void savePublicPractice_isCreatedForAdmin() throws Exception {
        // given
        final var dtoString = createPracticeDtoString(true);
        final var adminAuthHeader = getAuthHeader(mvc, USER_ADMIN);

        //when
        mvc.perform(post(PRACTICE_ENDPOINT)
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, adminAuthHeader))
                .andExpect(status().isCreated());

        //then
        final var all = practiceRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0)).matches(p ->
                p.isPublic() &&
                        p.getPracticeTag().getName().equals(TAG_NAME) &&
                        p.getData().equals(PRACTICE_DATA_JSON) &&
                        p.getDescription().equals(PRACTICE_DESC) &&
                        p.getName().equals(PRACTICE_NAME)
        );
    }

    @Test
    void saveUserPractice_isOk() throws Exception {
        //given
        final var dtoString = createPracticeDtoString(false);
        final var userAuthHeader = getAuthHeader(mvc, USER_1);

        //when
        mvc.perform(post(PRACTICE_ENDPOINT)
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, userAuthHeader))
                .andExpect(status().isCreated());

        //then
        final var practices = practiceRepository.findAll();
        assertThat(practices).hasSize(1);
        assertThat(practices.get(0)).matches(p ->
                !p.isPublic() &&
                p.getPracticeTag().getName().equals(TAG_NAME) &&
                p.getData().equals(PRACTICE_DATA_JSON) &&
                p.getDescription().equals(PRACTICE_DESC) &&
                p.getName().equals(PRACTICE_NAME)
        );

        final var practiceId = practices.get(0).getId();
        final var userId = userService.getUser(USER_1).getId();
        final var userPractices = userPracticeRepository.findAllByUserId(userId);
        assertThat(userPractices).hasSize(1);
        assertThat(userPractices.get(0).getPractice().getId()).isEqualTo(practiceId);
        assertThat(userPractices.get(0).getUser().getId()).isEqualTo(userId);
    }

    @Test
    void updatePracticeWithoutId_isBadRequest() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(false));
        final var practiceDto = PracticeDto.toDto(practice);
        practiceDto.id = null;
        final var dtoString = objectMapper.writeValueAsString(practiceDto);
        //when
        mvc.perform(put(PRACTICE_ENDPOINT)
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_1)))
        //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePublicPractice_isForbiddenForUserRole() throws Exception {
        // given
        final var practice = practiceRepository.save(createPractice(true));
        final var practiceDto = PracticeDto.toDto(practice);
        final var dtoString = objectMapper.writeValueAsString(practiceDto);
        //when
        mvc.perform(put(PRACTICE_ENDPOINT)
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_1)))
        //then
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUserPractice_isBadRequestForUserRole_ifUserDontOwnThisPractice() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(false));
        final var practiceDto = PracticeDto.toDto(practice);
        final var dtoString = objectMapper.writeValueAsString(practiceDto);
        //when
        mvc.perform(put(PRACTICE_ENDPOINT)
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_1)))
        //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePublicPractice_isAcceptedForAdminRole() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(true));
        final var practiceDto = PracticeDto.toDto(practice);
        final var newData = PRACTICE_DATA_JSON;
        practiceDto.data = objectMapper.readTree(newData);
        final var dtoString = objectMapper.writeValueAsString(practiceDto);
        //when
        mvc.perform(put(PRACTICE_ENDPOINT)
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_ADMIN)))
                .andExpect(status().isAccepted());

        //then
        final var practices = practiceRepository.findAll();
        assertThat(practices).hasSize(1);
        assertThat(practices.get(0).getData()).isEqualTo(newData);
    }

    @Test
    void updateUserPractice_isAcceptedForUserRole() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(false));
        addPracticeToUser(practice, USER_1);
        final var practiceDto = PracticeDto.toDto(practice);
        final var newData = PRACTICE_DATA_JSON;
        practiceDto.data = objectMapper.readTree(newData);
        final var dtoString = objectMapper.writeValueAsString(practiceDto);

        //when
        mvc.perform(put(PRACTICE_ENDPOINT)
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_1)))
                .andExpect(status().isAccepted());

        //then
        final var practices = practiceRepository.findAll();
        assertThat(practices).hasSize(1);
        assertThat(practices.get(0).getData()).isEqualTo(newData);
    }

    @Test
    void deletePublicPractice_adminRole_isOkTest() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(true));
        addPracticeToUser(practice, USER_1);
        final var dtoString = objectMapper.writeValueAsString(PracticeDto.toDto(practice));
        //when
        mvc.perform(delete(String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString()))
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_ADMIN)))
                .andExpect(status().isNoContent());
        //then
        assertThat(practiceRepository.findAll()).isEmpty();
        assertThat(userPracticeRepository.findAll()).isEmpty();
    }

    @Test
    void deleteUserPractice_adminRole_isForbiddenTest() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(false));
        addPracticeToUser(practice, USER_1);
        final var dtoString = objectMapper.writeValueAsString(PracticeDto.toDto(practice));
        //when
        mvc.perform(delete(String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString()))
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_ADMIN)))
        //then
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePublicUserPractice_adminRole_isForbiddenTest() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(true));
        addPracticeToUser(practice, USER_ADMIN);
        final var dtoString = objectMapper.writeValueAsString(PracticeDto.toDto(practice));
        //when
        mvc.perform(delete(String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString()))
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_ADMIN)))
        //then
                .andExpect(status().isNoContent());

        assertThat(practiceRepository.getById(practice.getId()))
                .isNotNull();
        assertThat(userPracticeRepository.findByUserAndPractice(getUser(USER_ADMIN), practice))
                .isNull();
    }

    @Test
    void deletePublicPractice_userRole_shouldDeleteUserPracticeTest() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(true));
        addPracticeToUser(practice, USER_1);
        final var dtoString = objectMapper.writeValueAsString(PracticeDto.toDto(practice));
        //when
        mvc.perform(delete(String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString()))
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_1)))
                .andExpect(status().isNoContent());
        //then
        assertThat(practiceRepository.findAll()).hasSize(1);
        assertThat(userPracticeRepository.findAll()).isEmpty();
    }

    @Test
    void deletePublicPractice_userRole_shouldNotDeleteUserPracticeIfNotHisPracticeTest() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(true));
        final var dtoString = objectMapper.writeValueAsString(PracticeDto.toDto(practice));
        //when
        mvc.perform(delete(String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString()))
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_1)))
                .andExpect(status().isNoContent());
        //then
        assertThat(practiceRepository.findAll()).hasSize(1);
        assertThat(userPracticeRepository.findAll()).isEmpty();
    }

    @Test
    void deleteUserPractice_userRole_shouldDeletePracticeAndUserPracticeTest() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(false));
        addPracticeToUser(practice, USER_1);
        final var dtoString = objectMapper.writeValueAsString(PracticeDto.toDto(practice));
        //when
        mvc.perform(delete(String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString()))
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_1)))
                .andExpect(status().isNoContent());
        //then
        assertThat(practiceRepository.findAll()).isEmpty();
        assertThat(userPracticeRepository.findAll()).isEmpty();
    }

    @Test
    void deleteUserPractice_userRole_shouldNotDeleteUserPracticeIfNotHisPracticeTest() throws Exception {
        //given
        final var practice = practiceRepository.save(createPractice(false));
        final var dtoString = objectMapper.writeValueAsString(PracticeDto.toDto(practice));
        //when
        mvc.perform(delete(String.format("%s/%s", PRACTICE_ENDPOINT, practice.getId().toString()))
                .content(dtoString)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader(mvc, USER_1)))
                .andExpect(status().isForbidden());
        //then
        assertThat(practiceRepository.findAll()).hasSize(1);
        assertThat(userPracticeRepository.findAll()).isEmpty();
    }

    private String createPracticeDtoString(final boolean isPublic) throws JsonProcessingException {
        return objectMapper.writeValueAsString(createPracticeDto(isPublic));
    }

    private PracticeDto createPracticeDto(final boolean isPublic) throws JsonProcessingException {
        final var dto = new PracticeDto();
        dto.isPublic = isPublic;
        dto.name = PRACTICE_NAME;
        dto.data = objectMapper.readTree(PRACTICE_DATA_JSON);
        dto.description = PRACTICE_DESC;
        dto.practiceTag = PracticeTagDto.toDto(tagRepository.findAll().get(0));
        return dto;
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
        practice.setData(PRACTICE_DATA_JSON);
        practice.setPublic(isPublic);

        return practice;
    }

    private User getUser(final String username) {
        return userService.getUser(username);
    }

}
