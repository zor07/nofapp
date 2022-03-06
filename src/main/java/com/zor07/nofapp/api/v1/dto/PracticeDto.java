package com.zor07.nofapp.api.v1.dto;

import com.zor07.nofapp.practice.Practice;

public class PracticeDto {

    public static PracticeDto toDto(final Practice practice) {
        return new PracticeDto(practice.getId(),
                PracticeTagDto.toDto(practice.getPracticeTag()),
                practice.getName(),
                practice.getDescription(),
                practice.getData(),
                practice.isPublic());
    }

    public static Practice toEntity(final PracticeDto practiceDto) {
        return new Practice(practiceDto.id,
                PracticeTagDto.toEntity(practiceDto.practiceTag),
                practiceDto.name,
                practiceDto.description,
                practiceDto.data,
                practiceDto.isPublic);
    }


    public Long id;
    public PracticeTagDto practiceTag;
    public String name;
    public String description;
    public String data;
    public boolean isPublic;

    public PracticeDto(Long id,
                       PracticeTagDto practiceTag,
                       String name,
                       String description,
                       String data,
                       boolean isPublic) {
        this.id = id;
        this.practiceTag = practiceTag;
        this.name = name;
        this.description = description;
        this.data = data;
        this.isPublic = isPublic;
    }

    public PracticeDto() {
    }

    @Override
    public String toString() {
        return "PracticeDto{" +
                "id=" + id +
                ", practiceTag=" + practiceTag +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", data='" + data + '\'' +
                ", isPublic=" + isPublic +
                '}';
    }
}
