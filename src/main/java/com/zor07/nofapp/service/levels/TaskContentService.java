package com.zor07.nofapp.service.levels;

import com.fasterxml.jackson.databind.JsonNode;
import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.entity.file.File;
import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.entity.level.TaskContent;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.TaskContentRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;

@Service
@Validated
@Transactional
public class TaskContentService {
    private static final String TASK_BUCKET = "task";
    private static final String TASK_FILE_KEY= "task_file";
    private final TaskContentRepository repository;
    private final TaskRepository taskRepository;
    private final FileRepository fileRepository;
    private final S3Service s3;

    public TaskContentService(final TaskContentRepository repository,
                              final TaskRepository taskRepository,
                              final FileRepository fileRepository,
                              final S3Service s3) {
        this.repository = repository;
        this.taskRepository = taskRepository;
        this.fileRepository = fileRepository;
        this.s3 = s3;
    }

    public void save(final @Valid TaskContent content) {
        repository.save(content);
    }
    public void deleteByLevelIdAndTaskId(final Long levelId, final Long taskId) {
        repository.deleteByLevelIdAndTaskId(levelId, taskId);
    }

    public void addVideo(final Long taskContentId, final MultipartFile data) throws IOException {
        final var taskContent = repository.getById(taskContentId);
        final var task = taskRepository.findByTaskContentId(taskContentId);
        final var bytes = data.getBytes();
        final var key = getFileKey(task.getId(), bytes);
        final var file = fileRepository.save(createFile(task, data, key));

        s3.persistObject(TASK_BUCKET, key, bytes);
        taskContent.setFile(file);
        repository.save(taskContent);
    }

    public void addText(final Long taskContentId, final JsonNode jsonNode) {
        final var taskContent = repository.getById(taskContentId);
        taskContent.setData(jsonNode.toString());
        save(taskContent);
    }

    private File createFile(final Task task, final MultipartFile data, final String key) {
        final var file = new File();
        file.setBucket(TASK_BUCKET);
        file.setPrefix(String.format("%s-%s", task.getLevel().getId(), task.getId()));
        file.setKey(key);
        file.setMime(data.getContentType());
        file.setSize(data.getSize());
        return file;
    }

    private String getFileKey(final Long taskId, final byte[] data) {
        final var hash = s3.getMD5(data);
        return String.format("%s/%s_%s", taskId, TASK_FILE_KEY, hash);
    }
}
