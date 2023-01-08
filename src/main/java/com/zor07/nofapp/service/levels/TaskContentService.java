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
import java.util.List;
import java.util.Objects;

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

    public List<TaskContent> getTaskContent(final Long levelId,
                                            final Long taskId) {
        final var task = taskRepository.findByLevelIdAndId(levelId, taskId);
        return repository.findAllByTaskId(task.getId());
    }

    @Transactional
    public void save(final Long levelId,
                     final Long taskId,
                     final @Valid TaskContent content) {
        final var task = taskRepository.findByLevelIdAndId(levelId, taskId);
        content.setTask(task);
        repository.save(content);
    }

    public void update(final Long levelId,
                       final Long taskId,
                       final @Valid TaskContent content) {
        final var task = taskRepository.findByLevelIdAndId(levelId, taskId);
        if (content.getTask() == null || !Objects.equals(content.getTask().getId(), task.getId())) {
            throw new IllegalArgumentException("wrong task content");
        }
        repository.save(content);
    }

    @Transactional
    public void deleteTaskContent(final Long levelId,
                                  final Long taskId,
                                  final Long taskContentId) {
        getTaskData(levelId, taskId, taskContentId);
        repository.deleteById(taskContentId);
    }

    @Transactional
    public void addVideo(final Long levelId,
                         final Long taskId,
                         final Long taskContentId,
                         final MultipartFile data) throws IOException {
        final var taskData = getTaskData(levelId, taskId, taskContentId);
        final var taskContent = repository.getById(taskContentId);
        final var bytes = data.getBytes();
        final var key = getFileKey(taskData.task.getId(), bytes);
        final var file = fileRepository.save(createFile(taskData.task, data, key));

        s3.persistObject(TASK_BUCKET, key, bytes);
        taskContent.setFile(file);
        repository.save(taskContent);
    }

    private TaskData getTaskData(final Long levelId,
                                 final Long taskId,
                                 final Long taskContentId) {
        final var task = taskRepository.findByLevelIdAndId(levelId, taskId);
        final var taskContent = repository.getById(taskContentId);
        if (!Objects.equals(task.getId(), taskContent.getTask().getId())) {
            throw new IllegalArgumentException("Wrong task content");
        }
        return new TaskData(task, taskContent);
    }

    private record TaskData(Task task, TaskContent taskContent) {
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
