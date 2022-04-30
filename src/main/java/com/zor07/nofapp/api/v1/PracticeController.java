package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.PracticeDto;
import com.zor07.nofapp.service.PracticeService;
import com.zor07.nofapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/practice")
public class PracticeController {

    private final UserService userService;
    private final PracticeService practiceService;
    @Autowired
    public PracticeController(final UserService userService,
                              final PracticeService practiceService) {
        this.userService = userService;
        this.practiceService = practiceService;
    }

    @GetMapping
    public List<PracticeDto> getPractices(@RequestParam(defaultValue = "false") final boolean isPublic,
                                          final Principal principal) {
        final var user = userService.getUser(principal);

        final var practices = isPublic
                ? practiceService.getPublicPractices()
                : practiceService.getUserPractices(user.getId());

        return practices.stream()
                .map(PracticeDto::toDto)
                .toList();
    }

    @GetMapping("/{practiceId}")
    public ResponseEntity<PracticeDto> getPractice(@PathVariable final Long practiceId, final Principal principal) {
        final var user = userService.getUser(principal);
        final var practice = practiceService.getPracticeForUser(practiceId, user);
        return ResponseEntity.ok(PracticeDto.toDto(practice));
    }

    @PostMapping("/{practiceId}/userPractice")
    public ResponseEntity<Void> addPracticeToUser(@PathVariable final Long practiceId, final Principal principal) {
        final var user = userService.getUser(principal);
        practiceService.addPracticeToUser(practiceId, user);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{practiceId}/userPractice")
    public ResponseEntity<Void> removePracticeFromUser(@PathVariable final Long practiceId, final Principal principal) {
        final var user = userService.getUser(principal);
        practiceService.removePracticeFromUser(practiceId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<PracticeDto> savePractice(@RequestBody final PracticeDto practiceDto, final Principal principal) {
        final var user = userService.getUser(principal);
        final var practice = practiceService.savePractice(PracticeDto.toEntity(practiceDto), user);
        final var uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(String.format("/api/v1/practice/%s", practice.getId()))
                .toUriString());
        return ResponseEntity.created(uri).body(PracticeDto.toDto(practice));
    }

    @PutMapping(consumes = "application/json")
    public ResponseEntity<PracticeDto> updatePractice(@RequestBody final PracticeDto practiceDto, final Principal principal) {
        final var user = userService.getUser(principal);
        final var practice = practiceService.updatePractice(PracticeDto.toEntity(practiceDto), user);
        return ResponseEntity.accepted().body(PracticeDto.toDto(practice));
    }

    @DeleteMapping("/{practiceId}")
    public ResponseEntity<Void> deletePractice(@PathVariable final Long practiceId, final Principal principal) {
        final var user = userService.getUser(principal);
        practiceService.deletePractice(practiceId, user);
        return ResponseEntity.noContent().build();
    }
}
