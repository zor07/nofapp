package com.zor07.nofapp.service;

import com.zor07.nofapp.entity.profile.RelapseLog;
import com.zor07.nofapp.repository.profile.RelapseLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelapseLogService {

    private final RelapseLogRepository relapseLogRepository;

    public RelapseLogService(RelapseLogRepository relapseLogRepository) {
        this.relapseLogRepository = relapseLogRepository;
    }

    public void save(final RelapseLog relapseLog) {
        relapseLogRepository.save(relapseLog);
    }

    public List<RelapseLog> getRelapseLogEntriesByUserId(final Long userId) {
        return relapseLogRepository.findAllByUserId(userId);
    }

    public void deleteRelapseLog(final Long relapseLogId, final Long userId) {
        relapseLogRepository.deleteByIdAndUserId(relapseLogId, userId);
    }
}
