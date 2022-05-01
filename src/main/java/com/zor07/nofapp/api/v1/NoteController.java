package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.NoteDto;
import com.zor07.nofapp.api.v1.mapper.NoteMapper;
import com.zor07.nofapp.service.NoteService;
import com.zor07.nofapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notebooks/{notebookId}/notes")
public class NoteController {

    private final UserService userService;
    private final NoteService noteService;
    private final NoteMapper noteMapper;
    public NoteController(final UserService userService,
                          final NoteService noteService,
                          final NoteMapper noteMapper) {
        this.userService = userService;
        this.noteService = noteService;
        this.noteMapper = noteMapper;
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteDto> getNote(final Principal principal,
                                           final @PathVariable Long notebookId,
                                           final @PathVariable Long noteId) {
        final var userId = userService.getUser(principal).getId();
        return ResponseEntity.ok(noteMapper.toDto(noteService.getNote(notebookId, noteId, userId)));
    }

    @GetMapping
    public ResponseEntity<List<NoteDto>> getNotesByBook(final Principal principal,
                                                        final @PathVariable Long notebookId) {
        final var userId = userService.getUser(principal).getId();
        final var notes = noteService.getNotes(notebookId, userId)
                .stream()
                .map(noteMapper::toDto)
                .toList();
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    public ResponseEntity<NoteDto> createNote(final Principal principal,
                                              final @PathVariable Long notebookId,
                                              final @RequestBody NoteDto dto) {

        final var user = userService.getUser(principal);
        final var note = noteService.saveNote(noteMapper.toEntity(dto, user));
        final var uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(String.format("/api/v1/notebooks/%d/notes/%d", note.getNotebook().getId(), note.getId()))
                .toUriString());
        return ResponseEntity.created(uri).body(noteMapper.toDto(note));
    }

    @PutMapping
    public ResponseEntity<NoteDto> updateNote(final Principal principal,
                                              final @PathVariable Long notebookId,
                                              final @RequestBody NoteDto dto) {
        final var user = userService.getUser(principal);
        final var note = noteService.updateNote(noteMapper.toEntity(dto, user));
        return ResponseEntity.accepted().body(noteMapper.toDto(note));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(final Principal principal,
                                           final @PathVariable Long notebookId,
                                           final @PathVariable Long noteId) {
        final var user = userService.getUser(principal);
        noteService.deleteNote(notebookId, noteId, user.getId());
        return ResponseEntity.noContent().build();
    }

}
