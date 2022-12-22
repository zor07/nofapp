package com.zor07.nofapp.domain.model.practice;


import com.zor07.nofapp.domain.validation.JsonString;

public record PracticeModel(
        Long id,
        PracticeTagModel practiceTag,
        String name,
        String description,
        @JsonString String data,
        boolean isPublic
) {


}
