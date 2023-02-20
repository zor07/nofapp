package com.zor07.nofapp.service.practice;

import com.zor07.nofapp.entity.practice.Practice;
import com.zor07.nofapp.entity.user.User;

import javax.validation.Valid;
import java.util.List;

public interface PracticeService {
    List<Practice> getPractices(boolean isPublic, Long userId);

    Practice getPracticeForUser(Long practiceId, User user);

    void addPracticeToUser(Long practiceId, User user);

    void removePracticeFromUser(Long practiceId, User user);

    Practice savePractice(@Valid Practice practice, User user);

    Practice updatePractice(@Valid Practice practice, User user);

    void deletePractice(Long practiceId, User user);
}
