package com.zor07.nofapp.aws.s3;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class S3Service implements AutoCloseable {

    public static class Config {

        private boolean auto = false;

        @Nullable
        private String endpoint;

        @Nullable
        private String region;

        @Nullable
        private String accessKey;

        @Nullable
        private String secretKey;

        public String getAccessKey() {
            return accessKey;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public String getRegion() {
            return region;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public boolean isAuto() {
            return auto;
        }

        public void setAccessKey(final String accessKey) {
            this.accessKey = accessKey;
        }

        public void setAuto(final boolean auto) {
            this.auto = auto;
        }

        public void setEndpoint(final String endpoint) {
            this.endpoint = endpoint;
        }

        public void setRegion(final String region) {
            this.region = region;
        }

        public void setSecretKey(final String secretKey) {
            this.secretKey = secretKey;
        }

    }

    /**
     * @see https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/javav2/example_code/s3
     */
    public static final int MAX_S3_FIND_OBJECTS_BATCH_SIZE = 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(S3Service.class);

    private static S3Client createClient(final Config config) {
        if (config.isAuto()) {
            LOGGER.info("Auto configuration");
            return S3Client.builder()
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        } else {
            LOGGER.info("Manual Configuration");
            final var creds = AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey());
            return S3Client.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(creds))
                    .region(Region.of(config.getRegion()))
                    .endpointOverride(URI.create(config.getEndpoint()))
                    .build();
        }
    }

    static S3ObjectRef createS3Object(final String bucket, final software.amazon.awssdk.services.s3.model.S3Object s3Object) {
        return new S3ObjectRef(
                bucket,
                s3Object.key(),
                s3Object.size(),
                s3Object.eTag(),
                s3Object.lastModified());
    }

    private final S3Client client;


    public S3Service(final Config config) {
        client = createClient(config);
    }

    @Override
    public void close() {
        try {
            client.close();
        } catch (Exception e) {
            //ignore
        }
    }

    public boolean containsBucket(final String name) {
        final var request = HeadBucketRequest.builder().bucket(name).build();
        try {
            client.headBucket(request);
        } catch (final NoSuchBucketException e) {
            return false;
        }
        return true;
    }

    public boolean containsObject(final S3ObjectRef s3ObjectRef) {
        return containsObject(s3ObjectRef.bucket, s3ObjectRef.key);
    }

    public boolean containsObject(final String bucket, final String key) {
        final var request = HeadObjectRequest.builder().bucket(bucket).key(key).build();
        try {
            client.headObject(request);
        } catch (final NoSuchKeyException e) {
            return false;
        }
        return true;
    }

    public void copyObject(final File src, final S3ObjectRef dest) {
        copyObject(src, dest.bucket, dest.key);
    }

    public void copyObject(final File src, final String destBucket, final String destKey) {
        final var request = PutObjectRequest.builder().bucket(destBucket).key(destKey).build();
        client.putObject(request, src.toPath());
    }

    public void copyObject(final S3ObjectRef src, final File dest) {
        copyObject(src.bucket, src.key, dest);
    }

    public void copyObject(final S3ObjectRef src, final S3ObjectRef dest) {
        copyObject(src.bucket, src.key, dest.bucket, dest.key);
    }

    public void copyObject(final String srcBucket, final String srcKey, final File dest) {
        if (dest.exists()) {
            dest.delete();
        }
        if (!dest.getParentFile().exists()) {
            try {
                FileUtils.createParentDirectories(dest);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        final var request = GetObjectRequest.builder()
                .bucket(srcBucket)
                .key(srcKey)
                .build();
        client.getObject(request, dest.toPath());
        if (!dest.exists()) {
            throw new RuntimeException(String.format("Can't copy %s:%s->%s", srcBucket, srcKey, dest));
        }
    }

    public void copyObject(final String srcBucket, final String srcKey, final String destBucket, final String destKey) {
        final var request = CopyObjectRequest.builder()
                .sourceBucket(srcBucket)
                .sourceKey(srcKey)
                .destinationBucket(destBucket)
                .destinationKey(destKey)
                .build();
        client.copyObject(request);
    }

    public void createBucket(final String name) {
        final var request = CreateBucketRequest.builder().bucket(name).build();
        client.createBucket(request);
    }

    public void createBucketIfNotExists(final String bucketName) {
        if (!containsBucket(bucketName)) {
            createBucket(bucketName);
        }
    }

    public void deleteBucket(final String name) {
        final var request = DeleteBucketRequest.builder().bucket(name).build();
        client.deleteBucket(request);
    }

    public void deleteObject(final S3ObjectRef ref) {
        deleteObject(ref.bucket, ref.key);
    }

    public void deleteObject(final String bucket, final String key) {
        LOGGER.debug("Delete S3 object. Bucket:{}, key:{}", bucket, key);
        final var request = DeleteObjectRequest.builder().bucket(bucket).key(key).build();
        client.deleteObject(request);
    }

    public ImmutableSet<String> findBuckets() {
        return client
                .listBuckets()
                .buckets()
                .stream()
                .map(Bucket::name)
                .collect(ImmutableSet.toImmutableSet());
    }

    public Optional<S3ObjectRef> findObject(final String bucket, final String key) {
        try {
            final var requestBuilder = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(key)
                    .maxKeys(1);
            final var batchResult = client.listObjectsV2(requestBuilder.build());
            if (batchResult.contents().isEmpty()) {
                return Optional.empty();
            }
            final var s3Object = batchResult.contents().get(0);
            return s3Object.key().equals(key)
                    ? Optional.of(createS3Object(bucket, s3Object))
                    : Optional.empty();
        } catch (final NoSuchKeyException e) {
            return Optional.empty();
        }
    }

    public Stream<S3ObjectRef> findObjects(final String bucket, final String prefix) {
        return findObjects(bucket, prefix, MAX_S3_FIND_OBJECTS_BATCH_SIZE);
    }

    public Stream<S3ObjectRef> findObjects(final String bucket, final String prefix, final int batchSize) {
        final var request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .maxKeys(batchSize)
                .build();
        return client
                .listObjectsV2Paginator(request)
                .stream()
                .flatMap(r -> r.contents().stream().map(s3Object -> createS3Object(bucket, s3Object)));
    }

    public ImmutableSet<String> findPrefixes(final String bucket, @NotNull final String rootPrefix) {
        return findPrefixes(bucket, "/", rootPrefix);
    }

    public ImmutableSet<String> findPrefixes(final String bucket, @NotNull final String delimiter, @NotNull final String rootPrefix) {
        Objects.requireNonNull(delimiter, "delimiter can't be null");
        Objects.requireNonNull(rootPrefix, "rootPrefix can't be null");
        String localRootPrefix = rootPrefix;
        final var isTopLevel = ("".equals(localRootPrefix) || localRootPrefix.equals(delimiter));
        if (!localRootPrefix.endsWith(delimiter)) {
            localRootPrefix += delimiter;
        }
        final var listObjectsRequest = isTopLevel
                ? ListObjectsRequest.builder().bucket(bucket).delimiter(delimiter).build()
                : ListObjectsRequest.builder().bucket(bucket).delimiter(delimiter).prefix(localRootPrefix).build();
        final var objects = client.listObjects(listObjectsRequest);
        return objects.commonPrefixes()
                .stream()
                .map(CommonPrefix::prefix)
                .collect(ImmutableSet.toImmutableSet());
    }

    public void persistObject(final S3ObjectRef ref, final byte[] content) {
        persistObject(ref.bucket, ref.key, content);
    }

    public void persistObject(final S3ObjectRef ref, final InputStream content, final long contentLength) {
        persistObject(ref.bucket, ref.key, content, contentLength);
    }

    public void persistObject(final String bucket, final String key, final byte[] content) {
        final var body = RequestBody.fromBytes(content);
        persistObject(bucket, key, body);
    }

    public void persistObject(final String bucket, final String key, final InputStream content, final long contentLength) {
        final var body = RequestBody.fromInputStream(content, contentLength);
        persistObject(bucket, key, body);
    }

    private void persistObject(final String bucket, final String key, final RequestBody body) {
        final var request = PutObjectRequest.builder().bucket(bucket).key(key).build();
        client.putObject(request, body);
    }

    public byte[] readObject(final S3ObjectRef ref) {
        return readObject(ref.bucket, ref.key);
    }

    public byte[] readObject(final String bucket, final String key) {
        final var request = GetObjectRequest.builder().bucket(bucket).key(key).build();
        return client.getObjectAsBytes(request).asByteArray();
    }

    public InputStream readObjectAsStream(final S3ObjectRef ref) {
        return readObjectAsStream(ref.bucket, ref.key);
    }

    public InputStream readObjectAsStream(final String bucket, final String key) {
        final var request = GetObjectRequest.builder().bucket(bucket).key(key).build();
        return client.getObject(request);
    }

    public void truncateBucket(final String bucket) {
        findObjects(bucket, "").forEach(this::deleteObject);
    }

    public String getMD5(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input);
            BigInteger number = new BigInteger(1, messageDigest);

            return number.toString(16);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn("Failed to calculate MD5 sum: {0}", e);
            throw new RuntimeException(e);
        }
    }

}
