package com.zor07.nofapp.practice.entity;

import javax.persistence.*;

@Entity
@Table(name = "practice_tag", schema = "public")
public class PracticeTagEntity {

  @Id
  @GeneratedValue(
          strategy = GenerationType.SEQUENCE,
          generator = "practice_tag_id_seq"
  )
  @SequenceGenerator(
          name = "practice_tag_id_seq",
          sequenceName = "practice_tag_id_seq",
          allocationSize = 1
  )
  private Long id;
  private String name;

  public PracticeTagEntity() {
  }

  public PracticeTagEntity(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
