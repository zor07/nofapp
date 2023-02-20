package com.zor07.nofapp.service.profile;

import com.zor07.nofapp.entity.notes.Note;
import com.zor07.nofapp.entity.user.User;

import javax.transaction.Transactional;
import java.util.List;

public interface UserPostService {
    List<Note> getUserPosts(Long userId);

    void addPostToUser(User user, Long noteId);

    @Transactional
    void removePostFromUser(Long userId, Long noteId);
}
