package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.DiaryIdAndTitle;
import com.zor07.nofapp.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Deprecated //soon will be replaced with Note
public interface DiaryRepository  extends JpaRepository<Diary, Long> {
  List<DiaryIdAndTitle> findAllByUserId(Long userId);
  void deleteByIdAndUserId(Long id, Long userId);
  Diary findByIdAndUserId(Long id, Long userId);
}
