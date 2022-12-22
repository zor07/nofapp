package com.zor07.nofapp.profile.entity;

import com.zor07.nofapp.file.entity.FileEntity;
import com.zor07.nofapp.user.entity.UserEntity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "profile", schema = "public")
public class ProfileEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "profile_id_seq"
    )
    @SequenceGenerator(
            name = "profile_id_seq",
            sequenceName = "profile_id_seq",
            allocationSize = 1
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    private Instant timerStart;

    @OneToOne
    @JoinColumn(name = "avatar_file_id", referencedColumnName = "id")
    private FileEntity avatar;

    public ProfileEntity() {
    }

    public ProfileEntity(Long id,
                         UserEntity user,
                         Instant timerStart,
                         FileEntity avatar) {
        this.id = id;
        this.user = user;
        this.timerStart = timerStart;
        this.avatar = avatar;
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

    public Instant getTimerStart() {
        return timerStart;
    }

    public void setTimerStart(Instant timerStart) {
        this.timerStart = timerStart;
    }

    public FileEntity getAvatar() {
        return avatar;
    }

    public void setAvatar(FileEntity avatar) {
        this.avatar = avatar;
    }
}