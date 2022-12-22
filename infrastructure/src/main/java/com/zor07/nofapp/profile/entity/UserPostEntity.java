package com.zor07.nofapp.profile.entity;

import com.zor07.nofapp.notes.entity.NoteEntity;
import com.zor07.nofapp.user.entity.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "user_posts", schema = "public")
public class UserPostEntity {

    @EmbeddedId
    private UserPostKeyEntity userPostKey = new UserPostKeyEntity();

    @OneToOne
    @MapsId("userId")
    private UserEntity user;

    @OneToOne
    @MapsId("noteId")
    private NoteEntity note;

    public UserPostEntity() {
    }

    public UserPostEntity(final UserEntity user,
                          final NoteEntity note) {
        this.user = user;
        this.note = note;
        this.userPostKey = new UserPostKeyEntity(user.getId(), note.getId());
    }

    public UserPostKeyEntity getUserPostKey() {
        return userPostKey;
    }

    public void setUserPostKey(UserPostKeyEntity userPostKey) {
        this.userPostKey = userPostKey;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public NoteEntity getNote() {
        return note;
    }

    public void setNote(NoteEntity note) {
        this.note = note;
    }
}
