package com.zor07.nofapp.model.notes;

import com.zor07.nofapp.model.user.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "notebook", schema = "public")
public class NotebookEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "notebook_id_seq"
    )
    @SequenceGenerator(
            name = "notebook_id_seq",
            sequenceName = "notebook_id_seq",
            allocationSize = 1
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    private String name;

    private String description;

    public NotebookEntity(Long id, UserEntity user, String name, String description) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.description = description;
    }

    public NotebookEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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
}
