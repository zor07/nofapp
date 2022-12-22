package com.zor07.nofapp.repository.file;

import com.zor07.nofapp.model.file.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

}
