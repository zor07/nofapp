package com.zor07.nofapp.domain.model.file;

public record File(
        Long id,
        String bucket,
        String prefix,
        String key,
        String mime,
        Long size
) {
}
