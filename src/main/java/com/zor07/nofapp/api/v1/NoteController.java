package com.zor07.nofapp.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zor07.nofapp.api.v1.dto.NoteDto;
import com.zor07.nofapp.repository.NotebookRepository;
import com.zor07.nofapp.repository.NoteRepository;
import com.zor07.nofapp.service.NoteService;
import com.zor07.nofapp.service.UserService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notebooks/{notebookId}/notes")
public class NoteController {

    private final NoteRepository noteRepository;
    private final NotebookRepository notebookRepository;
    private final UserService userService;
    private final NoteService noteService;
    public NoteController(final NoteRepository noteRepository,
                          final NotebookRepository notebookRepository,
                          final UserService userService,
                          final NoteService noteService) {
        this.noteRepository = noteRepository;
        this.notebookRepository = notebookRepository;
        this.userService = userService;
        this.noteService = noteService;
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteDto> getNote(final Principal principal,
                                           final @PathVariable Long notebookId,
                                           final @PathVariable Long noteId) throws JsonProcessingException {
        final var userId = userService.getUser(principal).getId();
        return ResponseEntity.ok(NoteDto.toDto(noteService.getNote(notebookId, noteId, userId)));
    }

    @GetMapping
    public ResponseEntity<List<NoteDto>> getNotesByBook(final Principal principal,
                                                        final @PathVariable Long notebookId) {
        final var userId = userService.getUser(principal).getId();
        final var notes = noteService.getNotes(notebookId, userId)
                .stream()
                .map(NoteDto::toDto)
                .toList();
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    public ResponseEntity<NoteDto> createNote(final Principal principal,
                                              final @PathVariable Long notebookId,
                                              final @RequestBody NoteDto dto) throws JsonProcessingException {

        final var user = userService.getUser(principal);
        if (notebookRepository.findByIdAndUserId(notebookId, user.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        final var note = NoteDto.toEntity(dto, user);
        final var saved = noteRepository.save(note);
        return new ResponseEntity<>(NoteDto.toDto(saved), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<NoteDto> updateNote(final Principal principal,
                                              final @PathVariable Long notebookId,
                                              final @RequestBody NoteDto note) throws JsonProcessingException {
        if (note.id == null) {
            return ResponseEntity.badRequest().build();
        }
        final var user = userService.getUser(principal);
        if (notebookRepository.findByIdAndUserId(notebookId, user.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        final var entity = NoteDto.toEntity(note, user);
        final var saved = noteRepository.save(entity);
        return ResponseEntity.accepted().body(NoteDto.toDto(saved));
    }

    @DeleteMapping("/{noteId}")
    @Transactional
    public ResponseEntity<Void> updateNote(final Principal principal,
                                           final @PathVariable Long notebookId,
                                           final @PathVariable Long noteId) {
        final var user = userService.getUser(principal);
        if (notebookRepository.findByIdAndUserId(notebookId, user.getId()) == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            noteRepository.deleteAllByIdAndNotebookId(noteId, notebookId);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
