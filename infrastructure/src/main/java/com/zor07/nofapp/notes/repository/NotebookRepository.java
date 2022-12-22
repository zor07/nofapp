package com.zor07.nofapp.notes.repository;

import com.zor07.nofapp.notes.entity.NotebookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotebookRepository extends JpaRepository<NotebookEntity, Long> {
  List<NotebookEntity> findAllByUserId(Long userId);
  void deleteByIdAndUserId(Long id, Long userId);
  NotebookEntity findByIdAndUserId(Long id, Long userId);
}
