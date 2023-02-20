package com.zor07.nofapp.service.notes;

import com.zor07.nofapp.entity.notes.Notebook;

import java.util.List;

public interface NotebookService {
    List<Notebook> getNotebooks(Long userId);

    Notebook getNotebook(Long notebookId, Long userId);

    Notebook saveNotebook(Notebook notebook);

    Notebook updateNotebook(Notebook notebook);

    void deleteNotebook(Long notebookId, Long userId);
}
