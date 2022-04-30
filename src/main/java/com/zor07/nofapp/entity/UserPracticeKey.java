package com.zor07.nofapp.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserPracticeKey implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "parctice_id")
    private Long practiceId;

    public UserPracticeKey(Long userId, Long practiceId) {
        this.userId = userId;
        this.practiceId = practiceId;
    }

    public UserPracticeKey() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Long practiceId) {
        this.practiceId = practiceId;
    }
}
