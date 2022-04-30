package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.Practice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeRepository extends JpaRepository<Practice, Long> {
    List<Practice> findByIsPublic(boolean isPublic);
}
