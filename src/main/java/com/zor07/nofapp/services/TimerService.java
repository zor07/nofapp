package com.zor07.nofapp.services;

import java.time.Duration;
import java.time.LocalDateTime;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

public class TimerService {


  public static class Config {

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startPoint;

    @Min(100)
    private int daysGoal;

    @Min(7)
    @Max(30)
    private int daysStep;

    public LocalDateTime getStartPoint() {
      return startPoint;
    }

    public void setStartPoint(LocalDateTime startPoint) {
      this.startPoint = startPoint;
    }

    public int getDaysGoal() {
      return daysGoal;
    }

    public void setDaysGoal(int daysGoal) {
      this.daysGoal = daysGoal;
    }

    public int getDaysStep() {
      return daysStep;
    }

    public void setDaysStep(int daysStep) {
      this.daysStep = daysStep;
    }
  }

  private final LocalDateTime startPoint;
  private final int daysGoal;
  private final int daysStep;
  private final int stepsCount;

  public TimerService(Config config) {
    this.startPoint = config.getStartPoint();
    this.daysGoal = config.getDaysGoal();
    this.daysStep = config.getDaysStep();
    this.stepsCount = daysGoal / daysStep + 1;
  }

  public String getStatus() {
    final var currentStepNumber = calculateCurrentStepNumber();
    final var start = calculateCurrentStepStartTime(currentStepNumber);
    final var end = calculateCurrentStepEndTime(currentStepNumber);
    final var s = calculateTimePassed();
    final var s1 = calculateTimeRemaining(start, end);
    return s + s1;
  }

  private String calculateTimePassed() {
    final var hoursPassed = Duration.between(startPoint, LocalDateTime.now()).toHours();
    final var daysPassed = Duration.between(startPoint, LocalDateTime.now()).toDays();
    final var weeksPassed = Duration.between(startPoint, LocalDateTime.now()).toDays() / 7;
    final var hoursTotal = Duration.between(startPoint,
        startPoint.plusDays(daysGoal)).toHours();
    final var weeksTotal = Duration.between(startPoint,
        startPoint.plusDays(daysGoal)).toDays() / 7 + 1;
    return  String.format("С начала трансформации прошло: " +
            "%d часов из %d " +
            "%d дней из %d " +
            "%d недель из %d. ",
        hoursPassed, hoursTotal,
        daysPassed, daysGoal,
        weeksPassed, weeksTotal);
  }

  private LocalDateTime calculateCurrentStepEndTime(final int currentStepNumber) {
    return startPoint
        .plusDays(currentStepNumber * daysStep);
  }

  private LocalDateTime calculateCurrentStepStartTime(final int currentStepNumber) {
    return startPoint
        .plusDays((currentStepNumber - 1) * daysStep);
  }

  private int calculateCurrentStepNumber() {
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
