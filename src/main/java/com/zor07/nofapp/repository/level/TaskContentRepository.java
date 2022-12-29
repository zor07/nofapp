package com.zor07.nofapp.repository.level;

import com.zor07.nofapp.entity.level.TaskContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface TaskContentRepository extends JpaRepository<TaskContent, Long> {

    @Query(
        nativeQuery = true,
        value = "delete from task_content where id in (select task_content_id from task where id = :taskId)"
    )
    @Modifying
    @Transactional
    void deleteByTaskId(Long taskId);
}


