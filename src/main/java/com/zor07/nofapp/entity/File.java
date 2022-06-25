package com.zor07.nofapp.entity;

import javax.persistence.*;

@Entity
@Table(name = "file", schema = "public")
public class File {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "file_id_seq"
    )
    @SequenceGenerator(
            name = "file_id_seq",
            sequenceName = "file_id_seq",
            allocationSize = 1
    )
    private Long id;
    private String bucket;
    private String prefix;
    private String key;
    private String mime;
    private Integer size;

    public File() {
    }

    public File(Long id,
                String bucket,
                String prefix,
                String key,
                String mime,
                Integer size) {
        this.id = id;
        this.bucket = bucket;
        this.prefix = prefix;
        this.key = key;
        this.mime = mime;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
