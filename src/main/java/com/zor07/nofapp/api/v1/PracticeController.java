package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.DiaryDto;
import com.zor07.nofapp.api.v1.dto.PracticeDto;
import com.zor07.nofapp.practice.Practice;
import com.zor07.nofapp.practice.PracticeRepository;
import com.zor07.nofapp.practice.UserPractice;
import com.zor07.nofapp.practice.UserPracticeKey;
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

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/practice")
// TODO create service and exception handler
public class PracticeController {

    private final UserService userService;
    private final PracticeRepository practiceRepository;
    private final UserPracticeRepository userPracticeRepository;

    @Autowired
    public PracticeController(final UserService userService,
                              final PracticeRepository practiceRepository,
                              final UserPracticeRepository userPracticeRepository) {
        this.userService = userService;
        this.practiceRepository = practiceRepository;
        this.userPracticeRepository = userPracticeRepository;
    }

    @GetMapping
    public List<PracticeDto> getPractices(@RequestParam(defaultValue = "false") final boolean isPublic,
                                          final Principal principal) throws Exception{
        final var user = getUser(principal);

        final var practices = isPublic
                ? practiceRepository.findByIsPublic(true)
                : userPracticeRepository.findAllByUserId(user.getId()).stream()
                                        .map(UserPractice::getPractice)
                                        .collect(Collectors.toList());

        return practices.stream()
                .map(practice -> PracticeDto.toDto(practice))
                .toList();
    }

    @GetMapping("/{practiceId}")
    public ResponseEntity<PracticeDto> getPractice(@PathVariable final Long practiceId, final Principal principal) {
        try {
            final var practice = practiceRepository.getById(practiceId);
            if (practice.isPublic()) {
                return new ResponseEntity<>(PracticeDto.toDto(practice), HttpStatus.OK);
            } else {
                final var user = getUser(principal);
                if (userPracticeRepository.findByUserAndPractice(user, practice) != null) {
                    return new ResponseEntity<>(PracticeDto.toDto(practice), HttpStatus.OK);
                }
            }
        } catch (final EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{practiceId}")
    public ResponseEntity<Void> addPracticeToUser(@PathVariable final Long practiceId, final Principal principal) {
        try {
            final var practice = practiceRepository.getById(practiceId);
            if (practice.isPublic()) {
                final var user = getUser(principal);
                if (userPracticeRepository.findByUserAndPractice(user, practice) == null) {
                    userPracticeRepository.save(
                            new UserPractice(new UserPracticeKey(user.getId(), practiceId), user, practice));
                    return new ResponseEntity<>(HttpStatus.ACCEPTED);
                } else {
                    return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (final EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping(consumes = "application/json")
    @Transactional
    public ResponseEntity<PracticeDto> savePractice(@RequestBody final PracticeDto practiceDto, final Principal principal) {
        final var user = getUser(principal);
        Practice practice;
        if (practiceDto.isPublic) {
            if (!SecurityUtils.isUserAdmin(user)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            practice = practiceRepository.save(PracticeDto.toEntity(practiceDto));
        } else {
            practice = practiceRepository.save(PracticeDto.toEntity(practiceDto));
            userPracticeRepository.save(new UserPractice(new UserPracticeKey(user.getId(), practice.getId()), user, practice));
        }
        return new ResponseEntity<>(PracticeDto.toDto(practice), HttpStatus.CREATED);
    }

    @PutMapping(consumes = "application/json")
    @Transactional
    public ResponseEntity<Void> updatePractice(@RequestBody final PracticeDto practiceDto, final Principal principal) {
        final var user = getUser(principal);
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
        final var user = getUser(principal);
        try {
            final var practice = practiceRepository.getById(practiceId);
            if (SecurityUtils.isUserAdmin(user)) {
                if (practice.isPublic()) {
                    userPracticeRepository.deleteAllByPractice(practice);
                    practiceRepository.delete(practice);
                } else {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            } else {
                if (practice.isPublic()) {
                    userPracticeRepository.deleteByUserAndPractice(user, practice);
                } else {
                    if (userPracticeRepository.findByUserAndPractice(user, practice) != null) {
                        userPracticeRepository.deleteByUserAndPractice(user, practice);
                        practiceRepository.delete(practice);
                    }
                }
            }
        } catch (final EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private User getUser(final Principal principal) {
        final var username = principal.getName();
        return userService.getUser(username);
    }

}
