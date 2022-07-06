package com.zor07.nofapp.api.v1.mapper;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.entity.Profile;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.spring.AbstractApplicationTest;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class ProfileMapperTest extends AbstractApplicationTest {

    private static final Long ID = 111L;
    private static final String USERNAME = "user";
    private static final String PASS = "pass";
    private static final Instant START_INSTANT = Instant.parse("2022-05-01T15:26:00Z");
    private static final LocalDateTime START_LOCAL_DATE_TIME = LocalDateTime.of(2022, 5, 1, 18, 26);
    private static final User USER = new User(null, USERNAME, USERNAME, PASS, Collections.emptyList());

    private static final String BUCKET = "profile-mapping-test";
    private static final String KEY = "Key";

    private static final com.zor07.nofapp.entity.File FILE = new com.zor07.nofapp.entity.File(null,
            BUCKET,
            null,
            KEY,
            "mime",
            null);
    private @Autowired ProfileMapper profileMapper;
    private @Autowired S3Service s3;
    private @Autowired WebApplicationContext context;
    private MockMvc mvc;

    @AfterClass
    void cleanUp() {
        s3.truncateBucket(BUCKET);
    }

    @BeforeClass
    void setUp() throws IOException {
        s3.createBucket(BUCKET);
        final var srcFile = new java.io.File("src/test/resources/logback-test.xml");
        s3.persistObject(BUCKET, KEY, FileUtils.readFileToByteArray(srcFile));

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldMapEntityToDto() throws MalformedURLException, URISyntaxException {
        //given
        final var entity = new Profile();
        entity.setUser(USER);
        entity.setTimerStart(START_INSTANT);
        entity.setAvatar(FILE);
        //when
        final var profileDto = profileMapper.toDto(entity, TimeZone.getTimeZone(ZoneId.systemDefault()), s3);

        //then
        assertThat(profileDto.id()).isEqualTo(entity.getId());
        assertThat(profileDto.timerStart()).isCloseTo(START_LOCAL_DATE_TIME, within(1, ChronoUnit.SECONDS));
        assertThat(profileDto.avatarUrl()).isNotEmpty();
        new URL(profileDto.avatarUrl()).toURI(); // should not throw exception
    }



}
