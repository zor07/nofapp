package com.zor07.nofapp.practice;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "practice_tag", schema = "public")
public class PracticeTag {

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

  public PracticeTag() {
  }

  public PracticeTag(Long id, String name) {
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
