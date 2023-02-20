package com.zor07.nofapp.service.notes.impl;

import com.zor07.nofapp.entity.notes.Note;
import com.zor07.nofapp.entity.notes.NoteIdAndTitle;
import com.zor07.nofapp.exception.IllegalResourceAccessException;
import com.zor07.nofapp.repository.notes.NoteRepository;
import com.zor07.nofapp.repository.notes.NotebookRepository;
import com.zor07.nofapp.service.notes.NoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final NotebookRepository notebookRepository;

    public NoteServiceImpl(final NoteRepository noteRepository,
                           final NotebookRepository notebookRepository) {
        this.noteRepository = noteRepository;
        this.notebookRepository = notebookRepository;
    }

    @Override
    public Note getNote(final Long notebookId, final Long noteId, final Long userId) {
        if (notUsersNotebook(userId, notebookId)) {
            throw new IllegalResourceAccessException();
        }
        return noteRepository.getById(noteId);
    }

    @Override
    public List<NoteIdAndTitle> getNotes(final Long notebookId, final Long userId) {
        if (notUsersNotebook(userId, notebookId)) {
            throw new IllegalResourceAccessException();
        }
        return noteRepository.findAllByNotebookId(notebookId);
    }

    @Override
    public Note saveNote(final @Valid Note note) {
        final var userId = note.getNotebook().getUser().getId();
        final var notebookId = note.getNotebook().getId();
        if (notUsersNotebook(userId, notebookId)) {
            throw new IllegalResourceAccessException();
        }
        return noteRepository.save(note);
    }

    @Override
    public Note updateNote(final @Valid Note note) {
        if (note.getId() == null) {
            throw new IllegalArgumentException();
        }
        return saveNote(note);
    }

    @Override
    public void deleteNote(final Long notebookId, final Long noteId, final Long userId) {
        if (notUsersNotebook(userId, notebookId)) {
            throw new IllegalResourceAccessException();
        }
        noteRepository.deleteAllByIdAndNotebookId(noteId, notebookId);
    }

    private boolean notUsersNotebook(final Long userId, final Long notebookId) {
        return notebookRepository.findByIdAndUserId(notebookId, userId) == null;
    }
}
