package com.zor07.nofapp.user.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "user", schema = "public")
public class UserEntity {
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "user_id_seq"
  )
  @SequenceGenerator(
      name = "user_id_seq",
      sequenceName = "user_id_seq",
      allocationSize = 1
  )
  private Long id;
  private String name;
  private String username;
  private String password;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name="user_roles",
      joinColumns=@JoinColumn(name="user_id"),
      inverseJoinColumns=@JoinColumn(name="role_id")
  )
  private Collection<RoleEntity> roles = new ArrayList<>();

  public UserEntity() {
  }

  public UserEntity(Long id, String name, String username, String password, Collection<RoleEntity> roles) {
    this.id = id;
    this.name = name;
    this.username = username;
    this.password = password;
    this.roles = roles;
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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Collection<RoleEntity> getRoles() {
    return roles;
  }

  public void setRoles(Collection<RoleEntity> roles) {
    this.roles = roles;
  }
}
