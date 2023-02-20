package com.zor07.nofapp.service.timer.impl;

import com.zor07.nofapp.entity.timer.Timer;
import com.zor07.nofapp.repository.timer.TimerRepository;
import com.zor07.nofapp.service.timer.TimerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class TimerServiceImpl implements TimerService {

    private final TimerRepository repository;

    public TimerServiceImpl(TimerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Timer> findAllByUserId(final Long userId) {
        return repository.findAllByUserId(userId);
    }

    @Override
    public void save(final Timer timer) {
        repository.save(timer);
    }

    @Override
    public void stopTimer(final Long timerId, final Long userId) {
        final var timer = repository.findByIdAndUserId(timerId, userId);
        if (timer != null) {
            timer.setStop(Instant.now());
            repository.save(timer);
        }
    }

    @Override
    public void deleteByIdAndUserId(final Long timerId, final Long userId) {
        repository.deleteByIdAndUserId(timerId, userId);
    }
}
