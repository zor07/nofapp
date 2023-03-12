package com.zor07.nofapp.service.levels.impl;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.entity.file.File;
import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import com.zor07.nofapp.service.levels.LevelService;
import com.zor07.nofapp.service.levels.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final String TASK_BUCKET = "task";
    private static final String TASK_FILE_KEY= "task_file";
    private final TaskRepository repository;
    private final LevelService levelService;
    private final FileRepository fileRepository;
    private final S3Service s3;

    public TaskServiceImpl(final TaskRepository repository,
                           final LevelService levelService,
                           final FileRepository fileRepository,
                           final S3Service s3) {
        this.repository = repository;
        this.levelService = levelService;
        this.fileRepository = fileRepository;
        this.s3 = s3;
    }

    @Override
    public List<Task> getAllByLevelId(final Long levelId) {
        return repository.findAllByLevelId(levelId);
    }

    @Override
    public Task getTask(final Long levelId, final Long taskId) {
        return repository.findByLevelIdAndId(levelId, taskId);
    }

    @Override
    @Transactional
    public Task save(Long levelId, final Task task) {
        final var level = levelService.findById(levelId);
        task.setLevel(level);
        return repository.save(task);
    }

    @Override
    public void delete(final Long levelId, final Long id) {
        repository.deleteByLevelIdAndId(levelId, id);
    }

    @Override
    public Task findNextTask(final Task task) {
        final var currentLevel = task.getLevel();
        var nextTask = repository.findNextTaskOfLevel(currentLevel.getId(), task.getOrder());
        if (nextTask == null) {
            final var nextLevel = levelService.findNextLevel(currentLevel);
            if (nextLevel == null) {
                return null;
            }
            nextTask = findFirstTaskOfLevel(nextLevel);
        }

        return nextTask;
    }

    @Override
    public Task findPrevTask(final Task task) {
        final var currentLevel = task.getLevel();
        var prevTask = repository.findPrevTaskOfLevel(currentLevel.getId(), task.getOrder());
        if (prevTask == null) {
            final var prevLevel = levelService.findPrevLevel(currentLevel);
            if (prevLevel == null) {
                return null;
            }
            prevTask = findLastTaskOfLevel(prevLevel);
        }

        return prevTask;
    }

    @Override
    public Task findFirstTaskOfLevel(final Level level) {
        return repository.findFirstTaskOfLevel(level.getId());
    }

    @Override
    public void addVideo(Long levelId, Long taskId, MultipartFile data) throws IOException {
        final var task = repository.findByLevelIdAndId(levelId, taskId);
        final var bytes = data.getBytes();
        final var key = getFileKey(taskId, bytes);
        final var file = fileRepository.save(createFile(task, data, key));

        s3.persistObject(TASK_BUCKET, key, bytes);
        task.setFile(file);
        repository.save(task);
    }

    private Task findLastTaskOfLevel(final Level level) {
        return repository.findLastTaskOfLevel(level.getId());
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
