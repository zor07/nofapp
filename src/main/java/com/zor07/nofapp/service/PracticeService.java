package com.zor07.nofapp.service;

import com.zor07.nofapp.entity.Practice;
import com.zor07.nofapp.exception.IllegalResourceAccessException;
import com.zor07.nofapp.entity.UserPractice;
import com.zor07.nofapp.entity.UserPracticeKey;
import com.zor07.nofapp.repository.UserPracticeRepository;
import com.zor07.nofapp.repository.PracticeRepository;
import com.zor07.nofapp.security.SecurityUtils;
import com.zor07.nofapp.entity.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@Service
@Transactional
public class PracticeService {

    private final PracticeRepository practiceRepository;
    private final UserPracticeRepository userPracticeRepository;

    public PracticeService(final PracticeRepository practiceRepository,
                           final UserPracticeRepository userPracticeRepository) {
        this.practiceRepository = practiceRepository;
        this.userPracticeRepository = userPracticeRepository;
    }

    public List<Practice> getPractices(final boolean isPublic, final Long userId) {
        return isPublic ? getPublicPractices() : getUserPractices(userId);
    }

    public Practice getPracticeForUser(final Long practiceId, final User user) {
        final var practice = practiceRepository.getById(practiceId);
        if (!practice.isPublic() && !isUsersPractice(user, practice)) {
            throw new IllegalResourceAccessException();
        }
        return practice;
    }

    public void addPracticeToUser(final Long practiceId, final User user) {
        final var practice = practiceRepository.getById(practiceId);
        if (practice.isPublic()) {
            if (!isUsersPractice(user, practice)) {
                userPracticeRepository.save(
                        new UserPractice(new UserPracticeKey(user.getId(), practiceId), user, practice));
            }
        } else {
            throw new IllegalResourceAccessException();
        }
    }

    public void removePracticeFromUser(final Long practiceId, final User user) {
        final var practice = practiceRepository.getById(practiceId);
        if (isUsersPractice(user, practice)) {
            userPracticeRepository.deleteByUserAndPractice(user, practice);
        }
    }

    public Practice savePractice(final @Valid Practice practice, final User user) {
        Practice saved;
        if (practice.isPublic()) {
            if (!SecurityUtils.isUserAdmin(user)) {
                throw new IllegalResourceAccessException();
            }
            saved = practiceRepository.save(practice);
        } else {
            saved = practiceRepository.save(practice);
            userPracticeRepository.save(new UserPractice(new UserPracticeKey(user.getId(), practice.getId()), user, practice));
        }
        return saved;
    }

    public Practice updatePractice(final @Valid Practice practice, final User user) {
        if (practice.getId() == null) {
            throw new IllegalArgumentException();
        }
        if (practice.isPublic() && !SecurityUtils.isUserAdmin(user)) {
            throw new IllegalResourceAccessException();
        }
        if (!practice.isPublic() && !isUsersPractice(user, practice)) {
            throw new IllegalResourceAccessException();
        }
        return practiceRepository.save(practice);
    }

    public void deletePractice(final Long practiceId, final User user) {
        final var practice = practiceRepository.getById(practiceId);
        if (practice.isPublic() ) {
            if (SecurityUtils.isUserAdmin(user)) {
                userPracticeRepository.deleteAllByPractice(practice);
                practiceRepository.delete(practice);
            } else {
                throw new IllegalResourceAccessException();
            }
        } else {
            if (isUsersPractice(user, practice)) {
                userPracticeRepository.deleteByUserAndPractice(user, practice);
                practiceRepository.delete(practice);
            } else {
                throw new IllegalResourceAccessException();
            }
        }
    }

    private List<Practice> getPublicPractices() {
        return practiceRepository.findByIsPublic(Boolean.TRUE);
    }

    private List<Practice> getUserPractices(final Long userId) {
        return userPracticeRepository.findAllByUserId(userId)
                .stream()
                .map(UserPractice::getPractice).toList();
    }

    private boolean isUsersPractice(final User user, final Practice practice) {
        return userPracticeRepository.findByUserAndPractice(user, practice) != null;
    }
}

