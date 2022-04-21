package com.zor07.nofapp.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zor07.nofapp.api.v1.dto.NoteDto;
import com.zor07.nofapp.notebook.NoteRepository;
import com.zor07.nofapp.notebook.NotebookRepository;
import com.zor07.nofapp.user.UserService;
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

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/{notebookId}/note")
public class NoteController {

    private final NoteRepository noteRepository;
    private final NotebookRepository notebookRepository;
    private final UserService userService;
    public NoteController(final NoteRepository noteRepository,
                          final NotebookRepository notebookRepository,
                          final UserService userService) {
        this.noteRepository = noteRepository;
        this.notebookRepository = notebookRepository;
        this.userService = userService;
    }
    @GetMapping
    public ResponseEntity<List<NoteDto>> getNotesByBook(final Principal principal,
                                                        final @PathVariable Long notebookId) {
        if (notebookRepository.findByIdAndUserId(notebookId, userService.getUser(principal).getId()) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final var notes = noteRepository.findAllByNotebookId(notebookId)
                .stream()
                .map(NoteDto::toDto)
                .toList();
        return new ResponseEntity<>(notes, HttpStatus.OK);
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

        return ResponseEntity.ok(NoteDto.toDto(saved));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteDto> getNote(final Principal principal,
                                           final @PathVariable Long notebookId,
                                           final @PathVariable Long noteId) throws JsonProcessingException {
        final var user = userService.getUser(principal);
        if (notebookRepository.findByIdAndUserId(notebookId, user.getId()) == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(NoteDto.toDto(noteRepository.getById(noteId)));
    }

    @PutMapping
    public ResponseEntity<NoteDto> updateNote(final Principal principal,
                                              final @PathVariable Long notebookId,
                                              final @RequestBody NoteDto note) throws JsonProcessingException {
        final var user = userService.getUser(principal);
        if (notebookRepository.findByIdAndUserId(notebookId, user.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        final var saved = noteRepository.save(NoteDto.toEntity(note, user));
        return ResponseEntity.ok(NoteDto.toDto(saved));
    }

    @DeleteMapping("/{noteId}")
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
