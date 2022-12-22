package com.zor07.nofapp.levels.entity;

import com.zor07.nofapp.file.entity.FileEntity;
import com.zor07.nofapp.validation.JsonString;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "task_content", schema = "public")
public class TaskContentEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "task_content_id_seq"
    )
    @SequenceGenerator(
            name = "task_content_id_seq",
            sequenceName = "task_content_id_seq",
            allocationSize = 1
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private FileEntity file;

    private String title;

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    @JsonString
    private String data;

    public TaskContentEntity() {
    }

    public TaskContentEntity(Long id, FileEntity file, String title, String data) {
        this.id = id;
        this.file = file;
        this.title = title;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
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
