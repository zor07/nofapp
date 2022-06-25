package com.zor07.nofapp.repository;

import com.zor07.nofapp.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

}
