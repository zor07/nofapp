package com.zor07.nofapp.api.v1.dto;

import com.zor07.nofapp.practice.PracticeTag;

public class PracticeTagDto {

    public static PracticeTagDto toDto(final PracticeTag practiceTag) {
        return new PracticeTagDto(practiceTag.getId(), practiceTag.getName());
    }

    public static PracticeTag toEntity(final PracticeTagDto practiceTagDto) {
        return new PracticeTag(practiceTagDto.getId(), practiceTagDto.getName());
    }

    private Long id;
    private String name;

    public PracticeTagDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
