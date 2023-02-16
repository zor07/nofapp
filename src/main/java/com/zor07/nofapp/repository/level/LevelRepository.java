package com.zor07.nofapp.repository.level;

import com.zor07.nofapp.entity.level.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LevelRepository  extends JpaRepository<Level, Long> {

    @Query(
            nativeQuery = true,
            value = """
                select *
                from level
                order by "order"
                limit 1"""
    )
    Level findFirstLevel();

    @Query(
            nativeQuery = true,
            value = """
                select *
                from level
                where "order" > :currentLevelOrder
                order by "order"
                limit 1"""
    )
    Level findNextLevel(Integer currentLevelOrder);

}
