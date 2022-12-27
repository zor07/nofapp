package com.zor07.nofapp.service.levels;

import com.fasterxml.jackson.databind.JsonNode;
import com.zor07.nofapp.entity.file.File;
import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.entity.level.TaskContent;
import com.zor07.nofapp.repository.level.TaskContentRepository;
import com.zor07.nofapp.repository.level.TaskRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskContentRepository taskContentRepository;

    public TaskService(final TaskRepository taskRepository,
                       final TaskContentRepository taskContentRepository) {
        this.taskRepository = taskRepository;
        this.taskContentRepository = taskContentRepository;
    }


    public List<Task> getAllByLevelId(Long levelId) {
        return taskRepository.findAllByLevelId(levelId);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void saveTaskContent(TaskContent content) {
        taskContentRepository.save(content);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public void deleteTaskContent(Long taskContentId) {
        taskContentRepository.deleteById(taskContentId);
    }

    public Task addVideoToTask(Long taskId, File file) {
        return null;
    }

    public Task addTextToTask(Long taskId, JsonNode jsonNode) {
        return null;
    }
}
