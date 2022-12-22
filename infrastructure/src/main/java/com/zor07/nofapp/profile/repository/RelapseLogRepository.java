package com.zor07.nofapp.profile.repository;

import com.zor07.nofapp.profile.entity.RelapseLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RelapseLogRepository extends JpaRepository<RelapseLogEntity, Long> {

    List<RelapseLogEntity> findAllByUserId(Long userId);

    @Transactional
    void deleteByIdAndUserId(Long id, Long userId);
}
