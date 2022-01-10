package com.zor07.nofapp.timer;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimerRepository extends JpaRepository<Timer, Long> {
  List<Timer> findAllByUserId(Long userId);
  void deleteByIdAndUserId(Long id, Long userId);
  Timer findByIdAndUserId(Long id, Long userId);
}
