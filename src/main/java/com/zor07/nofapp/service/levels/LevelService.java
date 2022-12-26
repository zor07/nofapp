package com.zor07.nofapp.service.levels;

import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.repository.level.LevelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LevelService {

    private final LevelRepository levelRepository;

    public LevelService(final LevelRepository levelRepository) {
        this.levelRepository = levelRepository;
    }

    public Level save(final Level level) {
        return levelRepository.save(level);
    }

    public List<Level> getAll() {
        return levelRepository.findAll();
    }

    public void delete(final Long id) {
        levelRepository.deleteById(id);
    }
}