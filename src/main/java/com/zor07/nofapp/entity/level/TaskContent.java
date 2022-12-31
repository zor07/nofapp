package com.zor07.nofapp.entity.level;

import com.zor07.nofapp.entity.file.File;
import com.zor07.nofapp.validation.NullableJsonString;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "task_content", schema = "public")
public class TaskContent {

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
    private File file;

    private String title;

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    @NullableJsonString
    private String data;

    public TaskContent() {
    }

    public TaskContent(Long id, File file, String title, String data) {
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
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
