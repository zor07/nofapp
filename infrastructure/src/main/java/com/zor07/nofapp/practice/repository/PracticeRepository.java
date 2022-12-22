package com.zor07.nofapp.practice.repository;

import com.zor07.nofapp.practice.entity.PracticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeRepository extends JpaRepository<PracticeEntity, Long> {
    List<PracticeEntity> findByIsPublic(boolean isPublic);
}
