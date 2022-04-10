package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.NoteDto;
import com.zor07.nofapp.notebook.Note;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/{notebookId}/note")
public class NoteController {

    @GetMapping
    public ResponseEntity<List<Note>> getNotesByBook(final @PathVariable Long notebookId) {
        return null;
    }

    @PostMapping
    public ResponseEntity<NoteDto> createNote(final @PathVariable Long notebookId,
                                              final @RequestBody NoteDto note) {
        return null;
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteDto> getNote(final @PathVariable Long notebookId,
                                           final @PathVariable Long noteId) {
        return null;
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteDto> updateNote(final @PathVariable Long notebookId,
                                              final @PathVariable Long noteId,
                                              final @RequestBody NoteDto note) {
        return null;
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> updateNote(final @PathVariable Long notebookId,
                                              final @PathVariable Long noteId) {
        return null;
    }

}
