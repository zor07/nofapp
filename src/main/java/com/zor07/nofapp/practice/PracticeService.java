package com.zor07.nofapp.practice;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PracticeService {

    private final PracticeRepository practiceRepository;

    public PracticeService(PracticeRepository practiceRepository) {
        this.practiceRepository = practiceRepository;
    }
}

