package com.zor07.nofapp.profile.repository;

import com.zor07.nofapp.profile.entity.UserPostEntity;
import com.zor07.nofapp.profile.entity.UserPostKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPostsRepository extends JpaRepository<UserPostEntity, UserPostKeyEntity> {

    List<UserPostEntity> findAllByUserId(Long userId);

    //TODO add test
    void deleteUserPostByUserIdAndNoteId(Long userId, Long noteId);

}