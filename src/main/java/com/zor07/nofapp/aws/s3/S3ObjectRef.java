package com.zor07.nofapp.aws.s3;

import javax.annotation.concurrent.Immutable;
import java.time.Instant;
import java.util.Objects;

@Immutable
public class S3ObjectRef {

    public final String bucket;

    public final String key;

    public final long size;

    public final String etag;

    public final Instant lastModified;

    public S3ObjectRef(String bucket, String key, long size, String hash, Instant lastModified) {
        this.bucket = bucket;
        this.key = key;
        this.size = size;
        this.etag = hash;
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        S3ObjectRef that = (S3ObjectRef) o;
        return size == that.size && bucket.equals(that.bucket) && key.equals(that.key) && Objects.equals(etag, that.etag) && Objects.equals(lastModified, that.lastModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bucket, key, size, etag, lastModified);
    }

    @Override
    public String toString() {
        return "S3ObjectRef{" +
                "bucket='" + bucket + '\'' +
                ", key='" + key + '\'' +
                ", size=" + size +
                ", etag='" + etag + '\'' +
                ", lastModified=" + lastModified +
                '}';
    }
}