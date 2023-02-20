package com.zor07.nofapp.service.notes.impl;

import com.zor07.nofapp.entity.notes.Notebook;
import com.zor07.nofapp.repository.notes.NotebookRepository;
import com.zor07.nofapp.service.notes.NotebookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotebookServiceImpl implements NotebookService {

    private final NotebookRepository repository;

    public NotebookServiceImpl(NotebookRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Notebook> getNotebooks(final Long userId) {
        return repository.findAllByUserId(userId);
    }

    @Override
    public Notebook getNotebook(final Long notebookId, final Long userId) {
        return repository.findByIdAndUserId(notebookId, userId);
    }

    @Override
    public Notebook saveNotebook(final Notebook notebook) {
        return repository.save(notebook);
    }

    @Override
    public Notebook updateNotebook(final Notebook notebook) {
        if (notebook.getId() == null) {
            throw new IllegalArgumentException();
        }
        return saveNotebook(notebook);
    }

    @Override
    public void deleteNotebook(final Long notebookId, final Long userId) {
        repository.deleteByIdAndUserId(notebookId, userId);
    }
}
