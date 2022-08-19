package com.zor07.nofapp.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "profile", schema = "public")
public class Profile {
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
    private User user;

    private Instant timerStart;

    @OneToOne
    @JoinColumn(name = "avatar_file_id", referencedColumnName = "id")
    private File avatar;

    public Profile() {
    }

    public Profile(Long id,
                   User user,
                   Instant timerStart,
                   File avatar) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getTimerStart() {
        return timerStart;
    }

    public void setTimerStart(Instant timerStart) {
        this.timerStart = timerStart;
    }

    public File getAvatar() {
        return avatar;
    }

    public void setAvatar(File avatar) {
        this.avatar = avatar;
    }
}