package com.zor07.nofapp.notebook;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotebookRepository extends JpaRepository<Notebook, Long> {
  List<Notebook> findAllByUserId(Long userId);
  void deleteByIdAndUserId(Long id, Long userId);
  Notebook findByIdAndUserId(Long id, Long userId);
}
