package com.zor07.nofapp.repository.timer;

import com.zor07.nofapp.model.timer.TimerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimerRepository extends JpaRepository<TimerEntity, Long> {
  List<TimerEntity> findAllByUserId(Long userId);
  void deleteByIdAndUserId(Long id, Long userId);
  TimerEntity findByIdAndUserId(Long id, Long userId);
}
