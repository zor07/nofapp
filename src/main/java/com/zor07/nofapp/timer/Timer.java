package com.zor07.nofapp.timer;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Timer {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  private Long id;

  private Instant start;

  private Instant stop;

  private String description;

  public Timer(Long id,
      Instant start,
      Instant stop,
      String description) {
    this.id = id;
    this.start = start;
    this.stop = stop;
    this.description = description;
  }

  public Timer() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Instant getStart() {
    return start;
  }

  public void setStart(Instant start) {
    this.start = start;
  }

  public Instant getStop() {
    return stop;
  }

  public void setStop(Instant stop) {
    this.stop = stop;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
