package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.PracticeDto;
import com.zor07.nofapp.practice.Practice;
import com.zor07.nofapp.practice.PracticeRepository;
import com.zor07.nofapp.practice.PracticeService;
import com.zor07.nofapp.practice.UserPracticeRepository;
import com.zor07.nofapp.security.SecurityUtils;
import com.zor07.nofapp.user.User;
import com.zor07.nofapp.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/practice")
// TODO create service and exception handler
public class PracticeController {

    private final UserService userService;
    private final PracticeRepository practiceRepository;
    private final UserPracticeRepository userPracticeRepository;
    private final PracticeService practiceService;

    @Autowired
    public PracticeController(final UserService userService,
                              final PracticeRepository practiceRepository,
                              final UserPracticeRepository userPracticeRepository,
                              final PracticeService practiceService) {
        this.userService = userService;
        this.practiceRepository = practiceRepository;
        this.userPracticeRepository = userPracticeRepository;
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

    @PutMapping("/{practiceId}")
    public ResponseEntity<Void> addPracticeToUser(@PathVariable final Long practiceId, final Principal principal) {
        final var user = userService.getUser(principal);
        practiceService.addPracticeToUser(practiceId, user);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(consumes = "application/json")
    @Transactional
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
    @Transactional
    public ResponseEntity<Void> updatePractice(@RequestBody final PracticeDto practiceDto, final Principal principal) {
        final var user = userService.getUser(principal);
        if (practiceDto.id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (practiceDto.isPublic && !SecurityUtils.isUserAdmin(user)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (!practiceDto.isPublic && userPracticeRepository.findAllByUserId(user.getId())
                .stream()
                .noneMatch(userPractice -> userPractice.getPractice().getId().equals(practiceDto.id))){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        practiceRepository.save(PracticeDto.toEntity(practiceDto));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{practiceId}")
    @Transactional
    public ResponseEntity<Void> deletePractice(@PathVariable final Long practiceId, final Principal principal) {
        final var user = userService.getUser(principal);
        try {
            final var practice = practiceRepository.getById(practiceId);
            if (SecurityUtils.isUserAdmin(user)) {
                if (practice.isPublic()) {
                    if (isUsersPractice(user, practice)) {
                        userPracticeRepository.deleteByUserAndPractice(user, practice);
                    } else {
                        userPracticeRepository.deleteAllByPractice(practice);
                        practiceRepository.delete(practice);
                    }
                } else {
                    if (isUsersPractice(user, practice)) {
                        userPracticeRepository.deleteByUserAndPractice(user, practice);
                    } else {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                }
            } else {
                if (practice.isPublic()) {
                    userPracticeRepository.deleteByUserAndPractice(user, practice);
                } else {
                    if (isUsersPractice(user, practice)) {
                        userPracticeRepository.deleteByUserAndPractice(user, practice);
                        practiceRepository.delete(practice);
                    } else {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                }
            }
        } catch (final EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private boolean isUsersPractice(final User user, final Practice practice) {
        return userPracticeRepository.findByUserAndPractice(user, practice) != null;
    }
}
