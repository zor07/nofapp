package com.zor07.nofapp.aws.s3;

import com.zor07.nofapp.spring.s3.S3TestContainer;
import org.testng.annotations.AfterSuite;

abstract public class AbstractS3Test {

    private static final S3TestContainer S3_TEST_CONTAINER = new S3TestContainer();

    @AfterSuite
    final protected void afterSuite() {
        if (S3_TEST_CONTAINER != null) {
            S3_TEST_CONTAINER.close();
        }
    }

    protected S3Service createS3Service() {
        if (S3_TEST_CONTAINER == null) {
            return null;
        }
        final var config = new S3Service.Config();
        config.setEndpoint(S3_TEST_CONTAINER.getEndpoint());
        config.setRegion(S3_TEST_CONTAINER.getRegion());
        config.setSecretKey(S3_TEST_CONTAINER.getSecretKey());
        config.setAccessKey(S3_TEST_CONTAINER.getAccessKey());
        config.setAuto(S3_TEST_CONTAINER.isAuto());
        return new S3Service(config);
    }

}
