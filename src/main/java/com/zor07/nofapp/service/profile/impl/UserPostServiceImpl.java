package com.zor07.nofapp.service.profile.impl;

import com.zor07.nofapp.entity.notes.Note;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.entity.profile.UserPost;
import com.zor07.nofapp.repository.notes.NoteRepository;
import com.zor07.nofapp.repository.profile.UserPostsRepository;
import com.zor07.nofapp.service.profile.UserPostService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserPostServiceImpl implements UserPostService {

    private final UserPostsRepository userPostsRepository;
    private final NoteRepository noteRepository;

    public UserPostServiceImpl(final UserPostsRepository userPostsRepository,
                               final NoteRepository noteRepository) {
        this.userPostsRepository = userPostsRepository;
        this.noteRepository = noteRepository;
    }

    @Override
    public List<Note> getUserPosts(final Long userId) {
        return userPostsRepository.findAllByUserId(userId)
                .stream()
                .map(UserPost::getNote)
                .toList();
    }

    @Override
    public void addPostToUser(final User user, final Long noteId) {
        final var post = new UserPost();
        post.setUser(user);
        post.setNote(noteRepository.getById(noteId));
        userPostsRepository.save(post);
    }

    @Override
    @Transactional
    public void removePostFromUser(final Long userId, final Long noteId){
        userPostsRepository.deleteUserPostByUserIdAndNoteId(userId, noteId);
    }

}
