package com.zor07.nofapp.service;

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

    public List<NoteIdAndTitle> getNotesByNotebookIdForUser(final Long notebookId, final Long userId) {
        if (notUsersNotebook(userId, notebookId)) {
            throw new IllegalResourceAccessException();
        }
        return noteRepository.findAllByNotebookId(notebookId);
    }

    private boolean notUsersNotebook(final Long userId, final Long notebookId) {
        return notebookRepository.findByIdAndUserId(notebookId, userId) == null;
    }

//    @GetMapping
//    public ResponseEntity<List<NoteDto>> getNotesByBook(final Principal principal,
//                                                        final @PathVariable Long notebookId) {
//        if (notebookRepository.findByIdAndUserId(notebookId, userService.getUser(principal).getId()) == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        final var notes = noteRepository.findAllByNotebookId(notebookId)
//                .stream()
//                .map(NoteDto::toDto)
//                .toList();
//        return new ResponseEntity<>(notes, HttpStatus.OK);
//    }
//
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
//
//    @GetMapping("/{noteId}")
//    public ResponseEntity<NoteDto> getNote(final Principal principal,
//                                           final @PathVariable Long notebookId,
//                                           final @PathVariable Long noteId) throws JsonProcessingException {
//        final var user = userService.getUser(principal);
//        if (notebookRepository.findByIdAndUserId(notebookId, user.getId()) == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(NoteDto.toDto(noteRepository.getById(noteId)));
//    }
//
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
