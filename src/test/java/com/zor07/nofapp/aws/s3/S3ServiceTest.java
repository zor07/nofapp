package com.zor07.nofapp.aws.s3;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class S3ServiceTest extends AbstractS3Test {

    private final Random random = new Random();

    @Test
    public void testBucketCrud() {
        try (var s3 = createS3Service()) {
            final var bucket = "xxxxx" + random.nextInt();
            { //init
                assertThat(s3.containsBucket(bucket)).isFalse();
            }
            { //create
                s3.createBucket(bucket);
                assertThat(s3.containsBucket(bucket)).isTrue();
                assertThat(s3.findBuckets().contains(bucket)).isTrue();
            }
            {//delete
                s3.deleteBucket(bucket);
                assertThat(s3.containsBucket(bucket)).isFalse();
            }
        }
    }

    @Test
    public void testCopyObjectAsFile() throws Exception {
        final var destFile = File.createTempFile("s3-test-download-object", null);
        try {
            try (var s3 = createS3Service()) {
                final var bucket = "xxxxx" + random.nextInt();
                final var key = "key.xml";
                final var srcFile = new File("src/test/resources/logback-test.xml");
                if (destFile.exists()) {
                    destFile.delete();
                }
                s3.createBucket(bucket);
                s3.copyObject(srcFile, bucket, key);
                s3.copyObject(bucket, key, destFile);
                assertThat(Files.readAllBytes(srcFile.toPath())).isEqualTo(Files.readAllBytes(destFile.toPath()));
            }
        } finally {
            if (destFile.exists()) {
                destFile.delete();
            }
        }
    }

    @Test
    public void testFindObjects() {
        try (var s3 = createS3Service()) {
            final var bucket = "xxxxx" + random.nextInt();
            s3.createBucket(bucket);
            final var objectTotal = 100;
            final var objectPrefix = "key";
            { //persist
                for (var i = 0; i < objectTotal; i++) {
                    final var objectKey = objectPrefix + i;
                    final var objectContent = String.valueOf(i).getBytes();
                    s3.persistObject(bucket, objectKey, objectContent);
                }
            }
            {//findObjects and delete
                final var s3ObjectsDefault = s3.findObjects(bucket, objectPrefix).collect(Collectors.toList());
                final var s3ObjectsBatch = s3.findObjects(bucket, objectPrefix, 3).collect(Collectors.toList());
                assertThat(s3ObjectsDefault.size()).isEqualTo(objectTotal);
                assertThat(s3ObjectsBatch.size()).isEqualTo(objectTotal);
                s3ObjectsDefault.forEach(s3::deleteObject);
            }
            s3.deleteBucket(bucket);
        }
    }

    @Test
    public void testObjectCrud() throws Exception {
        try (var s3 = createS3Service()) {
            final var bucket = "xxxxx" + random.nextInt();
            final var objectKey = "key.xml";
            final var objectContent = FileUtils.readFileToByteArray(new File("src/test/resources/logback-test.xml"));
            s3.createBucket(bucket);
            {//init
                assertThat(s3.containsObject(bucket, objectKey)).isFalse();
                assertThat(s3.findObject(bucket, objectKey).isPresent()).isFalse();
            }
            {//persist
                s3.persistObject(bucket, objectKey, objectContent);
                assertThat(s3.containsObject(bucket, objectKey)).isTrue();
                final var storedContent = IOUtils.toByteArray(s3.readObjectAsStream(bucket, objectKey));
                assertThat(objectContent).isEqualTo(storedContent);
                final var s3Object = s3.findObject(bucket, objectKey).get();
                assertThat((int) s3Object.size).isEqualTo(objectContent.length);
            }
            {//copy
                final var objectCopyKey = objectKey + "copy";
                assertThat(s3.containsObject(bucket, objectCopyKey)).isFalse();
                s3.copyObject(bucket, objectKey, bucket, objectCopyKey);
                assertThat(s3.containsObject(bucket, objectKey)).isTrue();
                assertThat(s3.containsObject(bucket, objectCopyKey)).isTrue();
                assertThat(s3.findObject(bucket, objectKey).get().etag)
                        .isEqualTo(s3.findObject(bucket, objectCopyKey).get().etag);
                s3.deleteObject(bucket, objectCopyKey);
                assertThat(s3.containsObject(bucket, objectCopyKey)).isFalse();
            }
            //delete
            {
                s3.deleteObject(bucket, objectKey);
                assertThat(s3.containsObject(bucket, objectKey)).isFalse();
                s3.deleteBucket(bucket);
            }
        }
    }

}
