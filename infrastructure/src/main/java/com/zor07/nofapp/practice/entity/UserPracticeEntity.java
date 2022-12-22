package com.zor07.nofapp.practice.entity;

import com.zor07.nofapp.user.entity.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "user_practice", schema = "public")
public class UserPracticeEntity {

    @EmbeddedId
    private UserPracticeKeyEntity id = new UserPracticeKeyEntity();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;


    @ManyToOne
    @MapsId("practiceId")
    @JoinColumn(name = "practice_id")
    private PracticeEntity practice;

    public UserPracticeEntity(UserPracticeKeyEntity id,
                              UserEntity user,
                              PracticeEntity practice) {
        this.id = id;
        this.user = user;
        this.practice = practice;
    }

    public UserPracticeEntity() {
    }

    public UserPracticeKeyEntity getId() {
        return id;
    }

    public void setId(UserPracticeKeyEntity id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public PracticeEntity getPractice() {
        return practice;
    }

    public void setPractice(PracticeEntity practice) {
        this.practice = practice;
    }
}
