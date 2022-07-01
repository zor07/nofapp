package com.zor07.nofapp.entity;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserPostKey implements Serializable {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "note_id", nullable = false)
    private Long noteId;

    public UserPostKey(Long userId, Long noteId) {
        this.userId = userId;
        this.noteId = noteId;
    }

    public UserPostKey() {
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
