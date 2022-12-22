package com.zor07.nofapp.model.practice;


import com.zor07.nofapp.validation.JsonString;

public record PracticeModel(
        Long id,
        PracticeTagModel practiceTag,
        String name,
        String description,
        @JsonString String data,
        boolean isPublic
) {


}
