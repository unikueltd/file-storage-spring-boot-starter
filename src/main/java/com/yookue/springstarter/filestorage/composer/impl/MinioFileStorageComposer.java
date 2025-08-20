/*
 * Copyright (c) 2025 Yookue Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yookue.springstarter.filestorage.composer.impl;


import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import com.yookue.commonplexus.javaseutil.constant.CharVariantConst;
import com.yookue.commonplexus.javaseutil.identity.JdkUuidGenerator;
import com.yookue.commonplexus.javaseutil.util.DurationUtilsWraps;
import com.yookue.commonplexus.javaseutil.util.LocalDateWraps;
import com.yookue.commonplexus.springutil.util.MinioConfigWraps;
import com.yookue.springstarter.filestorage.composer.FileStorageComposer;
import com.yookue.springstarter.filestorage.enumeration.FileStorageType;
import com.yookue.springstarter.filestorage.exception.FileStorageException;
import com.yookue.springstarter.filestorage.property.MinioFileStorageProperties;
import com.yookue.springstarter.filestorage.util.FileObjectStorageUtils;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;


/**
 * File storage composer for minio
 *
 * @author David Hsing
 */
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MinioFileStorageComposer implements FileStorageComposer, InitializingBean, DisposableBean {
    private final MinioFileStorageProperties properties;
    private final boolean concatDate;

    private MinioClient minioClient;

    @Override
    public void afterPropertiesSet() {
        minioClient = MinioConfigWraps.minioClient(properties);
    }

    @Override
    public boolean existsBucket() {
        if (StringUtils.isBlank(properties.getBucketName())) {
            return false;
        }
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucketName()).build());
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public boolean existsObject(@Nullable String objectKey, @Nullable String pathPrefix) {
        if (StringUtils.isAnyBlank(objectKey, properties.getBucketName())) {
            return false;
        }
        String objectPath = FileObjectStorageUtils.recurObjectPath(objectKey, pathPrefix);
        if (StringUtils.isBlank(objectPath)) {
            return false;
        }
        try {
            StatObjectArgs objectArgs = StatObjectArgs.builder().bucket(properties.getBucketName()).object(objectPath).build();
            minioClient.statObject(objectArgs);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public String uploadObject(@Nullable InputStream content, @Nullable String extension, @Nullable String pathPrefix, @Nullable Consumer<Object> metadataProvider) throws FileStorageException {
        if (content == null || StringUtils.isBlank(properties.getBucketName())) {
            return null;
        }
        String serialId = JdkUuidGenerator.getPopularId();
        LocalDate currentDate = LocalDateWraps.getCurrentDate();
        String objectKey = FileObjectStorageUtils.buildObjectKey(serialId, extension, concatDate, currentDate);
        String objectPath = FileObjectStorageUtils.buildObjectPath(serialId, extension, concatDate, currentDate, pathPrefix);
        try {
            Map<String, String> headers = new LinkedHashMap<>();
            if (metadataProvider != null) {
                metadataProvider.accept(headers);
            }
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(properties.getBucketName()).object(objectPath).stream(content, content.available(), -1).headers(headers).build();
            minioClient.putObject(objectArgs);
            return objectKey;
        } catch (Exception ex) {
            throw new FileStorageException(ex);
        }
    }

    @Override
    public InputStream downloadObject(@Nullable String objectKey, @Nullable String pathPrefix) throws FileStorageException {
        if (StringUtils.isAnyBlank(objectKey, properties.getBucketName())) {
            return null;
        }
        String objectPath = FileObjectStorageUtils.recurObjectPath(objectKey, pathPrefix);
        if (StringUtils.isBlank(objectPath)) {
            return null;
        }
        try {
            GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(properties.getBucketName()).object(objectPath).build();
            return minioClient.getObject(objectArgs);
        } catch (Exception ex) {
            throw new FileStorageException(ex);
        }
    }

    @Override
    public void removeObject(@Nullable String objectKey, @Nullable String pathPrefix) throws FileStorageException {
        if (StringUtils.isAnyBlank(objectKey, properties.getBucketName())) {
            return;
        }
        String objectPath = FileObjectStorageUtils.recurObjectPath(objectKey, pathPrefix);
        if (StringUtils.isBlank(objectPath)) {
            return;
        }
        try {
            RemoveObjectArgs objectArgs = RemoveObjectArgs.builder().bucket(properties.getBucketName()).object(objectPath).build();
            minioClient.removeObject(objectArgs);
        } catch (Exception ex) {
            throw new FileStorageException(ex);
        }
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public String getObjectUrl(@Nullable String objectKey, @Nullable String pathPrefix, @Nullable Duration expiration) {
        if (StringUtils.isAnyBlank(objectKey, properties.getBucketName())) {
            return null;
        }
        String objectPath = FileObjectStorageUtils.recurObjectPath(objectKey, pathPrefix);
        if (StringUtils.isBlank(objectPath)) {
            return null;
        }
        if (DurationUtilsWraps.isNotPositive(expiration)) {
            String endpoint = StringUtils.join(properties.getEndpoint(), CharVariantConst.COLON, properties.getPort());
            return FileObjectStorageUtils.buildObjectUrl(FileStorageType.MINIO, objectPath, null, endpoint, properties.getBucketName(), BooleanUtils.isTrue(properties.getSecureHttp()));
        }
        try {
            GetPresignedObjectUrlArgs objectArgs = GetPresignedObjectUrlArgs.builder().bucket(properties.getBucketName()).object(objectPath).method(Method.GET).expiry(DurationUtilsWraps.toSecondsInteger(expiration)).build();
            return minioClient.getPresignedObjectUrl(objectArgs);
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public Object getRawClient() {
        return minioClient;
    }

    @Nonnull
    @Override
    public FileStorageType getStorageType() {
        return FileStorageType.MINIO;
    }

    @Override
    public void destroy() {
        if (minioClient != null) {
            try {
                minioClient.close();
            } catch (Exception ignored) {
            }
        }
    }
}
