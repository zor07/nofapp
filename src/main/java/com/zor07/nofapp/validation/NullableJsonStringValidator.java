package com.zor07.nofapp.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NullableJsonStringValidator implements ConstraintValidator<NullableJsonString, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NullableJsonStringValidator.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void initialize(NullableJsonString jsonString) { }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext context) {
        if (string == null) {
            return true;
        }
        try {
            MAPPER.readTree(string);
            return true;
        } catch (JsonProcessingException e) {
            LOGGER.error("{} is not valid json string", string);
        }
        return false;
    }
}