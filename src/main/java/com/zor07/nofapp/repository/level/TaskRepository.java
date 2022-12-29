package com.zor07.nofapp.repository.level;

import com.zor07.nofapp.entity.level.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByLevelId(Long levelId);

    Task findByLevelIdAndId(Long levelId, Long id);

    Task findByTaskContentId(Long taskContentId);

    @Transactional
    void deleteByLevelIdAndId(Long levelId, Long id);

}
