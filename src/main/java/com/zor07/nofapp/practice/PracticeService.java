package com.zor07.nofapp.practice;

import com.zor07.nofapp.exception.IllegalResourceAccessException;
import com.zor07.nofapp.security.SecurityUtils;
import com.zor07.nofapp.user.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    public List<Practice> getPublicPractices() {
        return practiceRepository.findByIsPublic(Boolean.TRUE);
    }

    public List<Practice> getUserPractices(final Long userId) {
        return userPracticeRepository.findAllByUserId(userId)
                .stream()
                .map(UserPractice::getPractice).toList();
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

    public Practice savePractice(final Practice practice, final User user) {
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

    public Practice updatePractice(final Practice practice, final User user) {
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

    private boolean isUsersPractice(final User user, final Practice practice) {
        return userPracticeRepository.findByUserAndPractice(user, practice) != null;
    }

//    @DeleteMapping("/{practiceId}")
//    @Transactional
//    public ResponseEntity<Void> deletePractice(@PathVariable final Long practiceId, final Principal principal) {
//        final var user = userService.getUser(principal);
//        try {
//            final var practice = practiceRepository.getById(practiceId);
//            if (SecurityUtils.isUserAdmin(user)) {
//                if (practice.isPublic()) {
//                    if (isUsersPractice(user, practice)) {
//                        userPracticeRepository.deleteByUserAndPractice(user, practice);
//                    } else {
//                        userPracticeRepository.deleteAllByPractice(practice);
//                        practiceRepository.delete(practice);
//                    }
//                } else {
//                    if (isUsersPractice(user, practice)) {
//                        userPracticeRepository.deleteByUserAndPractice(user, practice);
//                    } else {
//                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//                    }
//                }
//            } else {
//                if (practice.isPublic()) {
//                    userPracticeRepository.deleteByUserAndPractice(user, practice);
//                } else {
//                    if (isUsersPractice(user, practice)) {
//                        userPracticeRepository.deleteByUserAndPractice(user, practice);
//                        practiceRepository.delete(practice);
//                    } else {
//                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//                    }
//                }
//            }
//        } catch (final EntityNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//

//
}

