package com.zor07.nofapp.timer;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
//
//    @PutMapping(path = "/{timerId}/stop")
//    @Transactional
//    // TODO accept stop time from client
//    public ResponseEntity<Void> stop(@PathVariable final Long timerId, final Principal principal) {
//        final var user = userService.getUser(principal);
//        try {
//            final var timer = repository.findByIdAndUserId(timerId, user.getId());
//            timer.setStop(Instant.now());
//            repository.save(timer);
//        } catch (EmptyResultDataAccessException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(HttpStatus.ACCEPTED);
//    }

    @Transactional
    public void deleteByIdAndUserId(final Long timerId, final Long  userId) {
        repository.deleteByIdAndUserId(timerId, userId);
    }

}
