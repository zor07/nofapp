package com.zor07.nofapp.user.entity;

import javax.persistence.*;

@Entity
@Table(name = "role", schema = "public")
public class RoleEntity {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "role_id_seq"
  )
  @SequenceGenerator(
      name = "role_id_seq",
      sequenceName = "role_id_seq",
      allocationSize = 1
  )
  private Long id;
  private String name;

  public RoleEntity() {
  }

  public RoleEntity(Long id, String name) {
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
