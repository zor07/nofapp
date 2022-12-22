package com.zor07.nofapp.repository.practice;

import com.zor07.nofapp.model.practice.PracticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeRepository extends JpaRepository<PracticeEntity, Long> {
    List<PracticeEntity> findByIsPublic(boolean isPublic);
}
