package com.zor07.nofapp.service.profile.impl;

import com.zor07.nofapp.entity.profile.RelapseLog;
import com.zor07.nofapp.repository.profile.RelapseLogRepository;
import com.zor07.nofapp.service.profile.RelapseLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelapseLogServiceImpl implements RelapseLogService {

    private final RelapseLogRepository relapseLogRepository;

    public RelapseLogServiceImpl(RelapseLogRepository relapseLogRepository) {
        this.relapseLogRepository = relapseLogRepository;
    }

    @Override
    public void save(final RelapseLog relapseLog) {
        relapseLogRepository.save(relapseLog);
    }

    @Override
    public List<RelapseLog> getRelapseLogEntriesByUserId(final Long userId) {
        return relapseLogRepository.findAllByUserId(userId);
    }

    @Override
    public void deleteRelapseLog(final Long relapseLogId, final Long userId) {
        relapseLogRepository.deleteByIdAndUserId(relapseLogId, userId);
    }
}
