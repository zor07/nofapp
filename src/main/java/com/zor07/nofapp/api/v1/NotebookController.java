package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.NotebookDto;
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
@RequestMapping("/api/v1/notebook")
public class NotebookController {

    private final NotebookRepository notebookRepository;
    private final UserService userService;

    public NotebookController(final NotebookRepository notebookRepository,
                              final UserService userService) {
        this.notebookRepository = notebookRepository;
        this.userService = userService;
    }

    @GetMapping
    public List<NotebookDto> getNotebooks(final Principal principal) {
        final var user = userService.getUser(principal);
        return notebookRepository.findAllByUserId(user.getId())
                .stream()
                .map(NotebookDto::toDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<Void> createNotebook(final Principal principal,
                                               final @RequestBody NotebookDto dto) {
        notebookRepository.save(NotebookDto.toEntity(dto, userService.getUser(principal)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{notebookId}")
    public NotebookDto getNotebook(final Principal principal,
                                                   final @PathVariable Long notebookId) {
        return NotebookDto.toDto(
                notebookRepository.findByIdAndUserId(notebookId, userService.getUser(principal).getId())
        );
    }

    @PutMapping("/{notebook}")
    public ResponseEntity<NotebookDto> updateNotebook(final Principal principal,
                                                      final @PathVariable NotebookDto notebook) {
        notebookRepository.save(NotebookDto.toEntity(notebook, userService.getUser(principal)));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{notebookId}")
    public ResponseEntity<Void> deleteNotebook(final Principal principal,
                                               final @PathVariable Long notebookId) {
        try {
            notebookRepository.deleteByIdAndUserId(notebookId, userService.getUser(principal).getId());
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.noContent().build();
    }

}
