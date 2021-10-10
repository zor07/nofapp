package com.zor07.nofapp.timer;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TimerService {

  private final TimerRepository repository;

  public TimerService(final TimerRepository repository) {
    this.repository = repository;
  }

  public TimerStatuses getStatuses() {
    final var statuses = new ArrayList<String>();
    repository.getTimers().forEach(timer -> {
      statuses.add(getStatus(timer));
    });
    return new TimerStatuses(statuses);
  }

  public void createTimer(final Timer timer) {
    repository.persist(timer);
  }

  private String getStatus(final Timer timer) {
    final var startPoint = timer.startPoint;
    final var daysGoal = timer.daysGoal;
    final var daysStep = timer.daysStep;
    final var stepsCount = daysGoal / daysStep + 1;


    final var currentStepNumber = calculateCurrentStepNumber(startPoint, daysGoal, daysStep, stepsCount);
    final var start = calculateCurrentStepStartTime(startPoint, daysStep, currentStepNumber);
    final var end = calculateCurrentStepEndTime(startPoint, daysStep, currentStepNumber);
    final var s = calculateTimePassed(startPoint, daysGoal, timer.description);
    final var s1 = calculateTimeRemaining(start, end);
    return s + s1;
  }

  private String calculateTimePassed(final LocalDateTime startPoint, final int daysGoal, final String description) {
    final var hoursPassed = Duration.between(startPoint, LocalDateTime.now()).toHours();
    final var daysPassed = Duration.between(startPoint, LocalDateTime.now()).toDays();
    final var weeksPassed = Duration.between(startPoint, LocalDateTime.now()).toDays() / 7;
    final var hoursTotal = Duration.between(startPoint,
        startPoint.plusDays(daysGoal)).toHours();
    final var weeksTotal = Duration.between(startPoint,
        startPoint.plusDays(daysGoal)).toDays() / 7 + 1;
    return  String.format("%s. Прошло времени: " +
            "%d часов из %d " +
            "%d дней из %d " +
            "%d недель из %d. ",
        description,
        hoursPassed, hoursTotal,
        daysPassed, daysGoal,
        weeksPassed, weeksTotal);
  }

  private LocalDateTime calculateCurrentStepEndTime(final LocalDateTime startPoint, final int daysStep, final int currentStepNumber) {
    return startPoint
        .plusDays(currentStepNumber * daysStep);
  }

  private LocalDateTime calculateCurrentStepStartTime(final LocalDateTime startPoint, final int daysStep, final int currentStepNumber) {
    return startPoint
        .plusDays((currentStepNumber - 1) * daysStep);
  }

  private int calculateCurrentStepNumber(final LocalDateTime startPoint, final int daysGoal, final int daysStep, final int stepsCount) {
    final var now = LocalDateTime.now();
    if (Duration.between(startPoint, now).toDays() > daysGoal) {
      throw new IllegalStateException("Анзор, ты продержался 100 дней," +
          " эта программа более не будет работать, и тебе она более не нужна.'");
    }
    var start = startPoint;
    for (int i = 1; i <= stepsCount; i++) {
      final var end = start.plusDays(daysStep);
      if (now.isAfter(start) && now.isBefore(end)) {
        return i;
      } else {
        start = end; // Как любит говорить Майя: "Конец - это начало." А у меня тут: "Начало - это конец."
      }
    }
    return 0;
  }

  private String calculateTimeRemaining(final LocalDateTime start, final LocalDateTime end)  {
    final var seconds = Duration.between(LocalDateTime.now(), end).toSeconds() -
        Duration.between(LocalDateTime.now(), end).toSeconds() / 60 * 60;
    final var minutes = Duration.between(LocalDateTime.now(), end).toMinutes() % 60;
    final var hours = Duration.between(LocalDateTime.now(), end).toHours();
    return String.format("Времени до конца текущей недели осталось: [%d:%02d:%02d]",
        hours,
        minutes,
        seconds);
  }


}
