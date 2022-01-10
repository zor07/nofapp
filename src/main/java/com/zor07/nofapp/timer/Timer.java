package com.zor07.nofapp.timer;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import com.zor07.nofapp.user.User;

@Entity
@Table(name = "timer", schema = "public")
public class Timer {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "timer_id_seq"
  )
  @SequenceGenerator(
      name = "timer_id_seq",
      sequenceName = "timer_id_seq",
      allocationSize = 1
  )
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  private Instant start;

  private Instant stop;

  private String description;

  public Timer(Long id, User user, Instant start, Instant stop, String description) {
    this.id = id;
    this.user = user;
    this.start = start;
    this.stop = stop;
    this.description = description;
  }

  public Timer() {
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
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
