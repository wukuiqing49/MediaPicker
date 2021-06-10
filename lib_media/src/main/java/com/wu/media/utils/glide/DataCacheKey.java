package com.wu.media.utils.glide;

/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2021/2/1 11:05
 * <p>
 * 名 字 : DataCacheKey
 * <p>
 * 简 介 :
 */

import com.bumptech.glide.load.Key;

import java.security.MessageDigest;

/**
 * A cache key for original source data + any requested signature.
 */
public class DataCacheKey implements Key {

    private final Key sourceKey;
    private final Key signature;

    public DataCacheKey(Key sourceKey, Key signature) {
        this.sourceKey = sourceKey;
        this.signature = signature;
    }

    public Key getSourceKey() {
        return sourceKey;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataCacheKey) {
            DataCacheKey other = (DataCacheKey) o;
            return sourceKey.equals(other.sourceKey) && signature.equals(other.signature);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = sourceKey.hashCode();
        result = 31 * result + signature.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DataCacheKey{"
                + "sourceKey=" + sourceKey
                + ", signature=" + signature
                + '}';
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        sourceKey.updateDiskCacheKey(messageDigest);
        signature.updateDiskCacheKey(messageDigest);
    }
}
