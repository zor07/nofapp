package com.zor07.nofapp.services;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class TimerService {

  static final int DAYS = 100;

  static final int STEP = 7;

  static final int CHECKPOINT_HOUR = 12;

  static final int STEPS_COUNT = DAYS / STEP + 1;

  static final LocalDateTime LIFE_TRANSFORMATION_START_DATE_TIME =
      LocalDateTime.of(2021, 9, 16, 12, 0, 0);

  public String getStatus() {
    final var currentStepNumber = calculateCurrentStepNumber();
    final var start = calculateCurrentStepStartTime(currentStepNumber);
    final var end = calculateCurrentStepEndTime(currentStepNumber);
    final var s = calculateTimePassed();
    final var s1 = calculateTimeRemaining(start, end);
    return s + s1;
  }

  static String calculateTimePassed() {
    final var hoursPassed = Duration.between(LIFE_TRANSFORMATION_START_DATE_TIME, LocalDateTime.now()).toHours();
    final var daysPassed = Duration.between(LIFE_TRANSFORMATION_START_DATE_TIME, LocalDateTime.now()).toDays();
    final var weeksPassed = Duration.between(LIFE_TRANSFORMATION_START_DATE_TIME, LocalDateTime.now()).toDays() / 7;
    final var hoursTotal = Duration.between(LIFE_TRANSFORMATION_START_DATE_TIME,
        LIFE_TRANSFORMATION_START_DATE_TIME.plusDays(DAYS)).toHours();
    final var weeksTotal = Duration.between(LIFE_TRANSFORMATION_START_DATE_TIME,
        LIFE_TRANSFORMATION_START_DATE_TIME.plusDays(DAYS)).toDays() / 7 + 1;
    return  String.format("С начала трансформации прошло: " +
            "%d часов из %d " +
            "%d дней из %d " +
            "%d недель из %d. ",
        hoursPassed, hoursTotal,
        daysPassed, DAYS,
        weeksPassed, weeksTotal);
  }

  static LocalDateTime calculateCurrentStepEndTime(final int currentStepNumber) {
    return LIFE_TRANSFORMATION_START_DATE_TIME
        .plusDays(currentStepNumber * STEP)
        .withHour(CHECKPOINT_HOUR);
  }

  static LocalDateTime calculateCurrentStepStartTime(final int currentStepNumber) {
    return LIFE_TRANSFORMATION_START_DATE_TIME
        .plusDays((currentStepNumber - 1) * STEP);
  }

  static int calculateCurrentStepNumber() {
    final var now = LocalDateTime.now();
    if (Duration.between(LIFE_TRANSFORMATION_START_DATE_TIME, now).toDays() > DAYS) {
      throw new IllegalStateException("Анзор, ты продержался 100 дней," +
          " эта программа более не будет работать, и тебе она более не нужна.'");
    }
    var start = LIFE_TRANSFORMATION_START_DATE_TIME;
    for (int i = 1; i <= STEPS_COUNT; i++) {
      final var end = start.plusDays(STEP);
      if (now.isAfter(start) && now.isBefore(end)) {
        return i;
      } else {
        start = end; // Как любит говорить Майя: "Конец - это начало." А у меня тут: "Начало - это конец."
      }
    }
    return 0;
  }

  static String calculateTimeRemaining(final LocalDateTime start, final LocalDateTime end)  {
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
