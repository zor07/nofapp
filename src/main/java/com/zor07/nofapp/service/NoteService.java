package com.zor07.nofapp.service;

import com.zor07.nofapp.entity.Note;
import com.zor07.nofapp.entity.NoteIdAndTitle;
import com.zor07.nofapp.exception.IllegalResourceAccessException;
import com.zor07.nofapp.repository.NoteRepository;
import com.zor07.nofapp.repository.NotebookRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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

    public Note getNote(final Long notebookId, final Long noteId, final Long userId) {
        if (notUsersNotebook(userId, notebookId)) {
            throw new IllegalResourceAccessException();
        }
        return noteRepository.getById(noteId);
    }

    public List<NoteIdAndTitle> getNotes(final Long notebookId, final Long userId) {
        if (notUsersNotebook(userId, notebookId)) {
            throw new IllegalResourceAccessException();
        }
        return noteRepository.findAllByNotebookId(notebookId);
    }

    public Note saveNote(final Note note) {
        final var userId = note.getNotebook().getUser().getId();
        final var notebookId = note.getNotebook().getId();
        if (notUsersNotebook(userId, notebookId)) {
            throw new IllegalResourceAccessException();
        }
        return noteRepository.save(note);
    }

    public Note updateNote(final Note note) {
        if (note.getId() == null) {
            throw new IllegalArgumentException();
        }
        return saveNote(note);
    }

    private boolean notUsersNotebook(final Long userId, final Long notebookId) {
        return notebookRepository.findByIdAndUserId(notebookId, userId) == null;
    }

//    @PostMapping
//    public ResponseEntity<NoteDto> createNote(final Principal principal,
//                                              final @PathVariable Long notebookId,
//                                              final @RequestBody NoteDto dto) throws JsonProcessingException {
//
//        final var user = userService.getUser(principal);
//        if (notebookRepository.findByIdAndUserId(notebookId, user.getId()) == null) {
//            return ResponseEntity.notFound().build();
//        }
//        final var note = NoteDto.toEntity(dto, user);
//        final var saved = noteRepository.save(note);
//        return new ResponseEntity<>(NoteDto.toDto(saved), HttpStatus.CREATED);
//    }

//    @PutMapping
//    public ResponseEntity<NoteDto> updateNote(final Principal principal,
//                                              final @PathVariable Long notebookId,
//                                              final @RequestBody NoteDto note) throws JsonProcessingException {
//        if (note.id == null) {
//            return ResponseEntity.badRequest().build();
//        }
//        final var user = userService.getUser(principal);
//        if (notebookRepository.findByIdAndUserId(notebookId, user.getId()) == null) {
//            return ResponseEntity.notFound().build();
//        }
//        final var entity = NoteDto.toEntity(note, user);
//        final var saved = noteRepository.save(entity);
//        return ResponseEntity.accepted().body(NoteDto.toDto(saved));
//    }
//
//    @DeleteMapping("/{noteId}")
//    @Transactional
//    public ResponseEntity<Void> updateNote(final Principal principal,
//                                           final @PathVariable Long notebookId,
//                                           final @PathVariable Long noteId) {
//        final var user = userService.getUser(principal);
//        if (notebookRepository.findByIdAndUserId(notebookId, user.getId()) == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        try {
//            noteRepository.deleteAllByIdAndNotebookId(noteId, notebookId);
//        } catch (EmptyResultDataAccessException e) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.noContent().build();
//    }




}
