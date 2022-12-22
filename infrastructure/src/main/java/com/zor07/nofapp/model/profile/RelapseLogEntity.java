package com.zor07.nofapp.model.profile;

import com.zor07.nofapp.model.user.UserEntity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "relapse_log", schema = "public")
public class RelapseLogEntity {

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
  private UserEntity user;

  private Instant start;

  private Instant stop;



  public RelapseLogEntity(Long id, UserEntity user, Instant start, Instant stop) {
    this.id = id;
    this.user = user;
    this.start = start;
    this.stop = stop;
  }

  public RelapseLogEntity() {
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
}
