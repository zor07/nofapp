package com.zor07.nofapp.service;

import com.zor07.nofapp.repository.NoteRepository;
import com.zor07.nofapp.repository.NotebookRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class NoteService {
    private final NoteRepository noteRepository;
    private final NotebookRepository notebookRepository;

    public NoteService(final NoteRepository noteRepository,
                       final NotebookRepository notebookRepository) {
        this.noteRepository = noteRepository;
        this.notebookRepository = notebookRepository;
    }



}
