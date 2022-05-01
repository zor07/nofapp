package com.zor07.nofapp.api.v1.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class PracticeDto {

    public Long id;
    public PracticeTagDto practiceTag;
    public String name;
    public String description;
    public JsonNode data;
    public boolean isPublic;

}
