package com.zor07.nofapp.api.v1.controller.level;

import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.spring.AbstractApiTest;
import com.zor07.nofapp.test.LevelTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.zor07.nofapp.test.UserTestUtils.DEFAULT_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LevelControllerTest extends AbstractApiTest {

    @Autowired
    private LevelRepository levelRepository;
    private static final String LEVELS_ENDPOINT  = "/api/v1/levels";
    private static final String LEVEL_ENDPOINT  = LEVELS_ENDPOINT + "/%s";

    @BeforeMethod
    public void setup() {
        tearDown();
        createDefaultUser();
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterClass
    void tearDown() {
        levelRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void createLevelTest() throws Exception {
        //given
        final var authHeader = getAuthHeader(mvc, DEFAULT_USERNAME);
        final var levelDto = LevelTestUtils.getBlankDto();

        //when
        final var resultActions = mvc.perform(post(url())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(levelDto))
                .header(HttpHeaders.AUTHORIZATION, authHeader));

        //then
        resultActions.andExpect(status().isCreated());
        final var result = levelRepository.findAll();
        assertThat(result).hasSize(1);
        final var actual = result.get(0);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getOrder()).isEqualTo(levelDto.order());
        assertThat(actual.getName()).isEqualTo(levelDto.name());
    }

    private String url() {
        return LEVELS_ENDPOINT;
    }
    private String url(final Long levelId) {
        return String.format(LEVEL_ENDPOINT, levelId);
    }
//
//    @Test
//    void getAllTest() {
//        levelRepository.save(LevelTestUtils.getBlankEntity());
//        levelRepository.save(LevelTestUtils.getBlankEntity());
//        levelRepository.save(LevelTestUtils.getBlankEntity());
//
//        final var result = levelService.getAll();
//
//        assertThat(result).hasSize(3);
//        assertThat(result).allSatisfy( level ->
//                LevelTestUtils.checkEntity(LevelTestUtils.getBlankEntity(), level, false)
//        );
//    }
//    @Test
//    void deleteTest() {
//        assertThat(levelRepository.findAll()).isEmpty();
//        final var level = levelRepository.save(LevelTestUtils.getBlankEntity());
//
//        levelService.delete(level.getId());
//        assertThat(levelRepository.findAll()).isEmpty();
//    }

}
