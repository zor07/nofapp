package com.zor07.nofapp.service.levels.impl;

import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.repository.level.LevelRepository;
import com.zor07.nofapp.service.levels.LevelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;

    public LevelServiceImpl(final LevelRepository levelRepository) {
        this.levelRepository = levelRepository;
    }

    @Override
    public Level save(final Level level) {
        return levelRepository.save(level);
    }

    @Override
    public List<Level> getAll() {
        return levelRepository.findAll();
    }

    @Override
    public void delete(final Long id) {
        levelRepository.deleteById(id);
    }

    @Override
    public Level findFirstLevel() {
        return levelRepository.findFirstLevel();
    }

    @Override
    public Level findNextLevel(final Level level) {
        return levelRepository.findNextLevel(level.getOrder());
    }
}
