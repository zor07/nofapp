package com.zor07.nofapp.model.notes;

import com.vladmihalcea.hibernate.type.json.JsonType;
import com.zor07.nofapp.validation.JsonString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Table(name = "note", schema = "public")
@TypeDef(
        name = "json",
        typeClass = JsonType.class
)
public class NoteEntity {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "note_id_seq"
  )
  @SequenceGenerator(
      name = "note_id_seq",
      sequenceName = "note_id_seq",
      allocationSize = 1
  )
  private Long id;

  @OneToOne
  @JoinColumn(name = "notebook_id", referencedColumnName = "id")
  private NotebookEntity notebook;

  private String title;

  @Type(type = "json")
  @Column(columnDefinition = "jsonb")
  @JsonString
  private String data;

  public NoteEntity(Long id, NotebookEntity notebook, String title, String data) {
    this.id = id;
    this.notebook = notebook;
    this.title = title;
    this.data = data;
  }

  public NoteEntity() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public NotebookEntity getNotebook() {
    return notebook;
  }

  public void setNotebook(NotebookEntity notebook) {
    this.notebook = notebook;
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
