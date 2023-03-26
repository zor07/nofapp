package com.zor07.nofapp.service.levels;

import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.entity.level.Task;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

public interface TaskService {
    List<Task> getAllByLevelId(Long levelId);

    Task getTask(Long levelId, Long taskId);

    @Transactional
    Task save(Long levelId, Task task);

    void delete(Long levelId, Long id);

    Task findNextTask(Task task);

    Task findPrevTask(Task task);

    Task findFirstTaskOfLevel(Level level);

    @Transactional
    void addVideo(Long levelId,
                  Long taskId,
                  MultipartFile data) throws IOException;

    @Transactional
    void deleteVideo(Long levelId,
                     Long taskId);

}
