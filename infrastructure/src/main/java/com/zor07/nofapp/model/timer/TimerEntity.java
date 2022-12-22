package com.zor07.nofapp.model.timer;

import com.zor07.nofapp.model.user.UserEntity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "timer", schema = "public")
public class TimerEntity {

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
  private UserEntity user;

  private Instant start;

  private Instant stop;

  private String description;

  public TimerEntity(Long id, UserEntity user, Instant start, Instant stop, String description) {
    this.id = id;
    this.user = user;
    this.start = start;
    this.stop = stop;
    this.description = description;
  }

  public TimerEntity() {
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
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
