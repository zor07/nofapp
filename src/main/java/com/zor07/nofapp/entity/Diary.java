package com.zor07.nofapp.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "diary", schema = "public")
@Deprecated //soon will be replaced with Note
public class Diary {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "diary_id_seq"
  )
  @SequenceGenerator(
      name = "diary_id_seq",
      sequenceName = "diary_id_seq",
      allocationSize = 1
  )
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  private String title;

  private String data;

  public Diary(Long id, User user, String title, String data) {
    this.id = id;
    this.user = user;
    this.title = title;
    this.data = data;
  }

  public Diary() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
