package com.zor07.nofapp.service.profile;

import com.zor07.nofapp.entity.profile.RelapseLog;

import java.util.List;

public interface RelapseLogService {
    void save(RelapseLog relapseLog);

    List<RelapseLog> getRelapseLogEntriesByUserId(Long userId);

    void deleteRelapseLog(Long relapseLogId, Long userId);
}
