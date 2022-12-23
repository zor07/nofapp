package com.zor07.nofapp.repository.profile;

import com.zor07.nofapp.entity.profile.UserPost;
import com.zor07.nofapp.entity.profile.UserPostKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPostsRepository extends JpaRepository<UserPost, UserPostKey> {

    List<UserPost> findAllByUserId(Long userId);

    //TODO add test
    void deleteUserPostByUserIdAndNoteId(Long userId, Long noteId);

}