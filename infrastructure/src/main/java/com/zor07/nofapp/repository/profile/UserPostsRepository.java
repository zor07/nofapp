package com.zor07.nofapp.repository.profile;

import com.zor07.nofapp.model.profile.UserPostEntity;
import com.zor07.nofapp.model.profile.UserPostKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPostsRepository extends JpaRepository<UserPostEntity, UserPostKeyEntity> {

    List<UserPostEntity> findAllByUserId(Long userId);

    //TODO add test
    void deleteUserPostByUserIdAndNoteId(Long userId, Long noteId);

}