package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.NotebookDto;
import com.zor07.nofapp.service.NotebookService;
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

import javax.transaction.Transactional;
import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notebooks")
public class NotebookController {

    private final NotebookService notebookService;
    private final UserService userService;

    public NotebookController(final NotebookService notebookService,
                              final UserService userService) {
        this.notebookService = notebookService;
        this.userService = userService;
    }

    @GetMapping("/{notebookId}")
    public ResponseEntity<NotebookDto> getNotebook(final Principal principal,
                                   final @PathVariable Long notebookId) {
        final var user = userService.getUser(principal);
        final var notebook = notebookService.getNotebook(notebookId, user.getId());
        return ResponseEntity.ok().body(NotebookDto.toDto(notebook));
    }

    @GetMapping
    public List<NotebookDto> getNotebooks(final Principal principal) {
        final var user = userService.getUser(principal);
        return notebookService.getNotebooks(user.getId())
                .stream()
                .map(NotebookDto::toDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<NotebookDto> createNotebook(final Principal principal,
                                                   final @RequestBody NotebookDto dto) {
        final var user = userService.getUser(principal);
        final var saved = notebookService.saveNotebook(NotebookDto.toEntity(dto, user));
        final var uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(String.format("/api/v1/notebooks/%d", saved.getId()))
                .toUriString());
        return ResponseEntity.created(uri).body(NotebookDto.toDto(saved));
    }

    @PutMapping("/{notebook}")
    public ResponseEntity<NotebookDto> updateNotebook(final Principal principal,
                                                      final @PathVariable NotebookDto notebook) {
        final var user = userService.getUser(principal);
        final var updated = notebookService.updateNotebook(NotebookDto.toEntity(notebook, user));
        return ResponseEntity.accepted().body(NotebookDto.toDto(updated));
    }

    @DeleteMapping("/{notebookId}")
    @Transactional
    public ResponseEntity<Void> deleteNotebook(final Principal principal,
                                               final @PathVariable Long notebookId) {
        final var user = userService.getUser(principal);
        notebookService.deleteNotebook(notebookId, user.getId());
        return ResponseEntity.noContent().build();
    }

}
