package com.zor07.nofapp.model.practice;

import com.vladmihalcea.hibernate.type.json.JsonType;
import com.zor07.nofapp.validation.JsonString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Table(name = "practice", schema = "public")
@TypeDef(
        name = "json",
        typeClass = JsonType.class
)
public class PracticeEntity {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "practice_id_seq"
  )
  @SequenceGenerator(
      name = "practice_id_seq",
      sequenceName = "practice_id_seq",
      allocationSize = 1
  )
  private Long id;

  @OneToOne
  @JoinColumn(name = "tag_id", referencedColumnName = "id")
  private PracticeTagEntity practiceTag;

  private String name;

  private String description;

  @Type(type = "json")
  @Column(columnDefinition = "jsonb")
  @JsonString
  private String data;

  private boolean isPublic;

  public PracticeEntity(Long id,
                        PracticeTagEntity practiceTag,
                        String name,
                        String description,
                        String data,
                        boolean isPublic) {
    this.id = id;
    this.practiceTag = practiceTag;
    this.name = name;
    this.description = description;
    this.data = data;
    this.isPublic = isPublic;
  }

  public PracticeEntity() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PracticeTagEntity getPracticeTag() {
    return practiceTag;
  }

  public void setPracticeTag(PracticeTagEntity practiceTag) {
    this.practiceTag = practiceTag;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public void setPublic(boolean aPublic) {
    isPublic = aPublic;
  }
}
