package com.zor07.nofapp.entity.profile;

import com.zor07.nofapp.entity.User;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "relapse_log", schema = "public")
public class RelapseLog {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "relapse_log_id_seq"
  )
  @SequenceGenerator(
      name = "relapse_log_id_seq",
      sequenceName = "relapse_log_id_seq",
      allocationSize = 1
  )
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  private Instant start;

  private Instant stop;



  public RelapseLog(Long id, User user, Instant start, Instant stop) {
    this.id = id;
    this.user = user;
    this.start = start;
    this.stop = stop;
  }

  public RelapseLog() {
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
}
