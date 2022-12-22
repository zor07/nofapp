package com.zor07.nofapp.repository.notes;

import com.zor07.nofapp.model.notes.NoteEntity;
import com.zor07.nofapp.model.notes.NoteIdAndTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
  List<NoteIdAndTitleEntity> findAllByNotebookId(Long notebookId);

  void deleteAllByIdAndNotebookId(Long id, Long notebookId);
}
