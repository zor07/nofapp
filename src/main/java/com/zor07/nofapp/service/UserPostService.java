package com.zor07.nofapp.service;

import com.zor07.nofapp.entity.Note;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.entity.UserPost;
import com.zor07.nofapp.repository.NoteRepository;
import com.zor07.nofapp.repository.UserPostsRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserPostService {

    private final UserPostsRepository userPostsRepository;
    private final NoteRepository noteRepository;

    public UserPostService(final UserPostsRepository userPostsRepository,
                           final NoteRepository noteRepository) {
        this.userPostsRepository = userPostsRepository;
        this.noteRepository = noteRepository;
    }

    public List<Note> getUserPosts(final Long userId) {
        return userPostsRepository.findAllByUserId(userId)
                .stream()
                .map(UserPost::getNote)
                .toList();
    }

    public void addPostToUser(final User user, final Long noteId) {
        final var post = new UserPost();
        post.setUser(user);
        post.setNote(noteRepository.getById(noteId));
        userPostsRepository.save(post);
    }

    @Transactional
    public void removePostFromUser(final Long userId, final Long noteId){
        userPostsRepository.deleteUserPostByUserIdAndNoteId(userId, noteId);
    }

}
