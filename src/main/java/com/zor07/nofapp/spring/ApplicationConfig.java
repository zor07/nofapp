package com.zor07.nofapp.spring;

import com.zor07.nofapp.aws.s3.S3Service;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties("config")
@Validated
public class ApplicationConfig {

    @NotNull
    @Valid
    private S3Service.Config s3;

    public S3Service.Config getS3() {
        return s3;
    }

    public void setS3(S3Service.Config s3) {
        this.s3 = s3;
    }
}
