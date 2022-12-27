package com.zor07.nofapp.service.levels;

import com.fasterxml.jackson.databind.JsonNode;
import com.zor07.nofapp.entity.file.File;
import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.entity.level.TaskContent;
import com.zor07.nofapp.repository.level.TaskContentRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class TaskContentService {

    private final TaskContentRepository repository;

    public TaskContentService(final TaskContentRepository repository) {
        this.repository = repository;
    }

    public void save(TaskContent content) {
        repository.save(content);
    }
    public void delete(Long taskContentId) {
        repository.deleteById(taskContentId);
    }

    public Task addVideo(Long taskId, File file) {
        return null;
    }

    public Task addText(Long taskId, JsonNode jsonNode) {
        return null;
    }
}
