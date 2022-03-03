package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.PracticeDto;
import com.zor07.nofapp.practice.PracticeRepository;
import com.zor07.nofapp.practice.UserPractice;
import com.zor07.nofapp.practice.UserPracticeRepository;
import com.zor07.nofapp.user.User;
import com.zor07.nofapp.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/practice")
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
                                          final Principal principal) {
        final var user = getUser(principal);

        final var practices = isPublic
                ? practiceRepository.findByIsPublic(true)
                : userPracticeRepository.findAllByUserId(user.getId()).stream()
                                        .map(UserPractice::getPractice)
                                        .collect(Collectors.toList());

        return practices.stream()
                .map(PracticeDto::toDto)
                .toList();
    }

    private User getUser(final Principal principal) {
        final var username = principal.getName();
        return userService.getUser(username);
    }

}