package com.zor07.nofapp.api.v1.dto;

import com.zor07.nofapp.practice.PracticeTag;

public class PracticeTagDto {

    public static PracticeTagDto toDto(final PracticeTag practiceTag) {
        return new PracticeTagDto(practiceTag.getId(), practiceTag.getName());
    }

    public static PracticeTag toEntity(final PracticeTagDto practiceTagDto) {
        return new PracticeTag(practiceTagDto.id, practiceTagDto.name);
    }

    public Long id;
    public String name;

    public PracticeTagDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
