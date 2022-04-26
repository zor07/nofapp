package com.zor07.nofapp.timer;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class TimerService {

    private final TimerRepository repository;

    public TimerService(TimerRepository repository) {
        this.repository = repository;
    }

//    @GetMapping(produces = "application/json")
//    public List<TimerDto> findAll(final Principal principal) {
//        final var user = userService.getUser(principal);
//        return repository.findAllByUserId(user.getId())
//                .stream()
//                .map(TimerDto::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @PostMapping(consumes = "application/json")
//    @Transactional
//    public ResponseEntity<Void> save(@RequestBody final TimerDto timer, final Principal principal) {
//        final var user = userService.getUser(principal);
//        repository.save(TimerDto.toEntity(timer, user));
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }

    public void save(final Timer timer) {
        repository.save(timer);
    }

    @Transactional
    public void stopTimer(final Long timerId, final Long userId) {
        final var timer = repository.findByIdAndUserId(timerId, userId);
        if (timer != null) {
            timer.setStop(Instant.now());
            repository.save(timer);
        }
    }

    @Transactional
    public void deleteByIdAndUserId(final Long timerId, final Long  userId) {
        repository.deleteByIdAndUserId(timerId, userId);
    }

}
