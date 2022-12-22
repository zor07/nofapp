package com.zor07.nofapp.file.repository;

import com.zor07.nofapp.file.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

}
