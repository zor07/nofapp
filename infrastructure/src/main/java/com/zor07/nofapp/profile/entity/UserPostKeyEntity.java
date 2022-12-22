package com.zor07.nofapp.profile.entity;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserPostKeyEntity implements Serializable {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "note_id", nullable = false)
    private Long noteId;

    public UserPostKeyEntity(Long userId, Long noteId) {
        this.userId = userId;
        this.noteId = noteId;
    }

    public UserPostKeyEntity() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }
}
