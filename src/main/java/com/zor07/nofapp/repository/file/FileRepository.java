package com.zor07.nofapp.repository.file;

import com.zor07.nofapp.entity.file.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

}
