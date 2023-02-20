package com.zor07.nofapp.service.notes;

import com.zor07.nofapp.entity.notes.Note;
import com.zor07.nofapp.entity.notes.NoteIdAndTitle;

import javax.validation.Valid;
import java.util.List;

public interface NoteService {
    Note getNote(Long notebookId, Long noteId, Long userId);

    List<NoteIdAndTitle> getNotes(Long notebookId, Long userId);

    Note saveNote(@Valid Note note);

    Note updateNote(@Valid Note note);

    void deleteNote(Long notebookId, Long noteId, Long userId);
}
