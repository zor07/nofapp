package com.zor07.nofapp.service;

import com.zor07.nofapp.entity.timer.Timer;
import com.zor07.nofapp.repository.TimerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class TimerService {

    private final TimerRepository repository;

    public TimerService(TimerRepository repository) {
        this.repository = repository;
    }

    public List<Timer> findAllByUserId(final Long userId) {
        return repository.findAllByUserId(userId);
    }

    public void save(final Timer timer) {
        repository.save(timer);
    }

    public void stopTimer(final Long timerId, final Long userId) {
        final var timer = repository.findByIdAndUserId(timerId, userId);
        if (timer != null) {
            timer.setStop(Instant.now());
            repository.save(timer);
        }
    }

    public void deleteByIdAndUserId(final Long timerId, final Long  userId) {
        repository.deleteByIdAndUserId(timerId, userId);
    }
}
