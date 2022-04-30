package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.Note;
import com.zor07.nofapp.entity.NoteIdAndTitle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
  List<NoteIdAndTitle> findAllByNotebookId(Long notebookId);

  void deleteAllByIdAndNotebookId(Long id, Long notebookId);
}
