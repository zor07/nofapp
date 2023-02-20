package com.zor07.nofapp.service.levels;

import com.zor07.nofapp.entity.level.TaskContent;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

public interface TaskContentService {
    List<TaskContent> getTaskContent(Long levelId,
                                     Long taskId);

    TaskContent getTaskContent(Long levelId,
                               Long taskId,
                               Long taskContentId);

    @Transactional
    void save(Long levelId,
              Long taskId,
              @Valid TaskContent content);

    void update(Long levelId,
                Long taskId,
                @Valid TaskContent content);

    @Transactional
    void deleteTaskContent(Long levelId,
                           Long taskId,
                           Long taskContentId);

    @Transactional
    void addVideo(Long levelId,
                  Long taskId,
                  Long taskContentId,
                  MultipartFile data) throws IOException;
}
