package com.zor07.nofapp.repository.notes;

import com.zor07.nofapp.model.notes.NotebookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotebookRepository extends JpaRepository<NotebookEntity, Long> {
  List<NotebookEntity> findAllByUserId(Long userId);
  void deleteByIdAndUserId(Long id, Long userId);
  NotebookEntity findByIdAndUserId(Long id, Long userId);
}
