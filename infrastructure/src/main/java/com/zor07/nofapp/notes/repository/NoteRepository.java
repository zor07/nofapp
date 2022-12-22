package com.zor07.nofapp.notes.repository;

import com.zor07.nofapp.notes.entity.NoteEntity;
import com.zor07.nofapp.notes.entity.NoteIdAndTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
  List<NoteIdAndTitleEntity> findAllByNotebookId(Long notebookId);

  void deleteAllByIdAndNotebookId(Long id, Long notebookId);
}
