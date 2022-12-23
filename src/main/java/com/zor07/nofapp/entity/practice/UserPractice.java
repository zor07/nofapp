package com.zor07.nofapp.entity.practice;

import com.zor07.nofapp.entity.User;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "user_practice", schema = "public")
public class UserPractice {

    @EmbeddedId
    private UserPracticeKey id = new UserPracticeKey();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne
    @MapsId("practiceId")
    @JoinColumn(name = "practice_id")
    private Practice practice;

    public UserPractice(UserPracticeKey id,
                        User user,
                        Practice practice) {
        this.id = id;
        this.user = user;
        this.practice = practice;
    }

    public UserPractice() {
    }

    public UserPracticeKey getId() {
        return id;
    }

    public void setId(UserPracticeKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Practice getPractice() {
        return practice;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
    }
}
