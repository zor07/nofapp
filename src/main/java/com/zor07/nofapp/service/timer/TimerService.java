package com.zor07.nofapp.service.timer;

import com.zor07.nofapp.entity.timer.Timer;

import java.util.List;

public interface TimerService {
    List<Timer> findAllByUserId(Long userId);

    void save(Timer timer);

    void stopTimer(Long timerId, Long userId);

    void deleteByIdAndUserId(Long timerId, Long userId);
}
