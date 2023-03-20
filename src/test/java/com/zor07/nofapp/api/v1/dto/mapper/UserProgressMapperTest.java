package com.zor07.nofapp.api.v1.dto.mapper;

import com.zor07.nofapp.api.v1.dto.level.mapper.TaskMapper;
import com.zor07.nofapp.api.v1.dto.userprogress.UserProgressDto;
import com.zor07.nofapp.api.v1.dto.userprogress.mapper.UserProgressMapper;
import com.zor07.nofapp.entity.userprogress.UserProgress;
import com.zor07.nofapp.test.LevelTestUtils;
import com.zor07.nofapp.test.TaskTestUtils;
import com.zor07.nofapp.test.UserTestUtils;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProgressMapperTest {

    private final UserProgressMapper mapper = new UserProgressMapper(new TaskMapper());

    @Test
    void toDtoTest() {
        final var user = UserTestUtils.createUser();
        final var level = LevelTestUtils.getBlankEntity();
        final var t1 = TaskTestUtils.getBlankEntity(1L, level);
        final var t2 = TaskTestUtils.getBlankEntity(2L, level);
        final var t3 = TaskTestUtils.getBlankEntity(3L, level);
        final var t4 = TaskTestUtils.getBlankEntity(4L, level);

        final var userProgressList = Stream.of(t1, t2, t3, t4)
                .map(it -> new UserProgress(it.getId(), user, it, Instant.now()))
                .peek(it -> {
                    if (it.getId() == 4L) {
                        it.setCompletedDatetime(null);
                    }
                })
                .toList();

        final var result = mapper.toDto(userProgressList);

        assertThat(result.uncompletedTask().task().id()).isEqualTo(4L);
        assertThat(result.uncompletedTask().completed()).isFalse();
        assertThat(result.userTasks()).hasSize(4);
        assertThat(result.userTasks().stream().filter(UserProgressDto.UserTaskDto::completed)).hasSize(3);
        assertThat(result.userTasks().stream().filter(it -> !it.completed())).hasSize(1);
    }

}
