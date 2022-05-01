package com.zor07.nofapp.api.v1.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class PracticeDto {

    public Long id;
    public PracticeTagDto practiceTag;
    public String name;
    public String description;
    public JsonNode data;
    public boolean isPublic;

    public PracticeDto(Long id,
                       PracticeTagDto practiceTag,
                       String name,
                       String description,
                       JsonNode data,
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
