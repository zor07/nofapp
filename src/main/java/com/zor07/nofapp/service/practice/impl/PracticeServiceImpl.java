package com.zor07.nofapp.service.practice.impl;

import com.zor07.nofapp.entity.practice.Practice;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.entity.practice.UserPractice;
import com.zor07.nofapp.entity.practice.UserPracticeKey;
import com.zor07.nofapp.exception.IllegalResourceAccessException;
import com.zor07.nofapp.repository.practice.PracticeRepository;
import com.zor07.nofapp.repository.practice.UserPracticeRepository;
import com.zor07.nofapp.security.SecurityUtils;
import com.zor07.nofapp.service.practice.PracticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@Transactional
public class PracticeServiceImpl implements PracticeService {

    private final PracticeRepository practiceRepository;
    private final UserPracticeRepository userPracticeRepository;

    public PracticeServiceImpl(final PracticeRepository practiceRepository,
                               final UserPracticeRepository userPracticeRepository) {
        this.practiceRepository = practiceRepository;
        this.userPracticeRepository = userPracticeRepository;
    }

    @Override
    public List<Practice> getPractices(final boolean isPublic, final Long userId) {
        return isPublic ? getPublicPractices() : getUserPractices(userId);
    }

    @Override
    public Practice getPracticeForUser(final Long practiceId, final User user) {
        final var practice = practiceRepository.getById(practiceId);
        if (!practice.isPublic() && !isUsersPractice(user, practice)) {
            throw new IllegalResourceAccessException();
        }
        return practice;
    }

    @Override
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

    @Override
    public void removePracticeFromUser(final Long practiceId, final User user) {
        final var practice = practiceRepository.getById(practiceId);
        if (isUsersPractice(user, practice)) {
            userPracticeRepository.deleteByUserAndPractice(user, practice);
        }
    }

    @Override
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

    @Override
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

    @Override
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

