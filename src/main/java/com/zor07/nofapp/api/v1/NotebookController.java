package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.NotebookDto;
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
@RequestMapping("/api/v1/notebook")
public class NotebookController {

    @GetMapping
    public ResponseEntity<List<NotebookDto>> getNotebooks() {
        return null;
    }

    @PostMapping
    public ResponseEntity<NotebookDto> createNotebook(final @RequestBody NotebookDto notebookDto) {
        return null;
    }

    @GetMapping("/{notebookId}")
    public ResponseEntity<NotebookDto> getNotebook(final @PathVariable Long notebookId) {
        return null;
    }

    @PutMapping("/{notebookId}")
    public ResponseEntity<NotebookDto> updateNotebook(final @PathVariable Long notebookId) {
        return null;
    }

    @DeleteMapping("/{notebookId}")
    public ResponseEntity<Void> deleteNotebook(final @PathVariable Long notebookId) {
        return ResponseEntity.noContent().build();
    }

}
