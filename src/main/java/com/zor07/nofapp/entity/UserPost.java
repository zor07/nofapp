package com.zor07.nofapp.entity;

import javax.persistence.*;

@Entity
@Table(name = "user_posts", schema = "public")
public class UserPost {

    @EmbeddedId
    private UserPostKey userPostKey = new UserPostKey();

    @OneToOne
    @MapsId("userId")
    private User user;

    @OneToOne
    @MapsId("noteId")
    private Note note;

    public UserPost() {
    }

    public UserPost(final User user,
                    final Note note) {
        this.user = user;
        this.note = note;
        this.userPostKey = new UserPostKey(user.getId(), note.getId());
    }

    public UserPostKey getUserPostKey() {
        return userPostKey;
    }

    public void setUserPostKey(UserPostKey userPostKey) {
        this.userPostKey = userPostKey;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }
}
