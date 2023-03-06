package com.zor07.nofapp.repository.level;

import com.zor07.nofapp.entity.level.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(
            nativeQuery = true,
            value = """
                select t.*
                from task t
                where t.level_id = :levelId
                order by t."order"
                limit 1"""
    )
    Task findFirstTaskOfLevel(Long levelId);

    @Query(
            nativeQuery = true,
            value = """
                select t.*
                from task t
                where t.level_id = :levelId
                order by t."order" desc 
                limit 1"""
    )
    Task findLastTaskOfLevel(Long levelId);

    @Query(
            nativeQuery = true,
            value = """
                select t.*
                from task t
                where t.level_id = :levelId
                  and t."order" > :currentTaskOrder
                order by t."order"
                limit 1"""
    )
    Task findNextTaskOfLevel(Long levelId, Integer currentTaskOrder);

    @Query(
            nativeQuery = true,
            value = """
                select t.*
                from task t
                where t.level_id = :levelId
                  and t."order" < :currentTaskOrder
                order by t."order"
                limit 1"""
    )
    Task findPrevTaskOfLevel(Long levelId, Integer currentTaskOrder);

    List<Task> findAllByLevelId(Long levelId);

    Task findByLevelIdAndId(Long levelId, Long id);

    @Transactional
    void deleteByLevelIdAndId(Long levelId, Long id);

}
