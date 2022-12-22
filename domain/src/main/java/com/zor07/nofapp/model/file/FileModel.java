package com.zor07.nofapp.model.file;

public record FileModel(
        Long id,
        String bucket,
        String prefix,
        String key,
        String mime,
        Long size
) {
}
