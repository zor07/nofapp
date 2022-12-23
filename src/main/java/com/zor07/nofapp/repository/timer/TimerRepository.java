package com.zor07.nofapp.repository.timer;

import java.util.List;

import com.zor07.nofapp.entity.timer.Timer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimerRepository extends JpaRepository<Timer, Long> {
  List<Timer> findAllByUserId(Long userId);
  void deleteByIdAndUserId(Long id, Long userId);
  Timer findByIdAndUserId(Long id, Long userId);
}
