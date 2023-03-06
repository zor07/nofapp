package com.zor07.nofapp.service.levels;

import com.zor07.nofapp.entity.level.Level;

import java.util.List;

public interface LevelService {
    Level save(Level level);

    List<Level> getAll();

    void delete(Long id);

    Level findFirstLevel();

    Level findNextLevel(Level level);

    Level findPrevLevel(Level level);
}
