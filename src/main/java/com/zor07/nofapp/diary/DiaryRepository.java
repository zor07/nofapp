package com.zor07.nofapp.diary;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository  extends JpaRepository<Diary, Long> {
  List<Diary> findAllByUserId(Long userId);
  void deleteByIdAndUserId(Long id, Long userId);
  Diary findByIdAndUserId(Long id, Long userId);
}
