package com.zor07.nofapp.repository.profile;

import com.zor07.nofapp.entity.profile.RelapseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RelapseLogRepository extends JpaRepository<RelapseLog, Long> {

    List<RelapseLog> findAllByUserId(Long userId);

    @Transactional
    void deleteByIdAndUserId(Long id, Long userId);
}
