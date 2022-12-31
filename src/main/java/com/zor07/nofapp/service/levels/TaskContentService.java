package com.zor07.nofapp.service.levels;

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

    @Transactional
    public void save(final Long levelId,
                     final Long taskId,
                     final @Valid TaskContent content) {
        final var task = taskRepository.findByLevelIdAndId(levelId, taskId);
        final var taskContent =  repository.save(content);
        task.setTaskContent(taskContent);
        taskRepository.save(task);
    }

    @Transactional
    public void deleteByLevelIdAndTaskId(final Long levelId, final Long taskId) {
        final var task = taskRepository.getById(taskId);
        final var taskContentId = task.getTaskContent().getId();
        if (taskContentId != null) {
            final var taskContent = repository.getById(taskContentId);
            final var file = taskContent.getFile();
            if (file != null) {
                s3.deleteObject(file.getBucket(), file.getKey());
            }
        }

        repository.deleteByLevelIdAndTaskId(levelId, taskId);
    }

    @Transactional
    public void addVideo(final Long levelId,
                         final Long taskId,
                         final MultipartFile data) throws IOException {
        final var task = taskRepository.findByLevelIdAndId(levelId, taskId);
        final var taskContentId = task.getTaskContent().getId();
        final var taskContent = repository.getById(taskContentId);
        final var bytes = data.getBytes();
        final var key = getFileKey(task.getId(), bytes);
        final var file = fileRepository.save(createFile(task, data, key));

        s3.persistObject(TASK_BUCKET, key, bytes);
        taskContent.setFile(file);
        repository.save(taskContent);
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
