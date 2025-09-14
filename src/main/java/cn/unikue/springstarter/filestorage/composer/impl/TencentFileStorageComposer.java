/*
 * Copyright (c) 2025 Unikue Ltd. All rights reserved.
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

package cn.unikue.springstarter.filestorage.composer.impl;


import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import java.util.function.Consumer;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import cn.unikue.commonplexus.javaseutil.identity.JdkUuidGenerator;
import cn.unikue.commonplexus.javaseutil.util.DurationUtilsWraps;
import cn.unikue.commonplexus.javaseutil.util.JdkDateWraps;
import cn.unikue.commonplexus.javaseutil.util.LocalDateWraps;
import cn.unikue.springstarter.filestorage.composer.FileStorageComposer;
import cn.unikue.springstarter.filestorage.enumeration.FileStorageType;
import cn.unikue.springstarter.filestorage.event.FileStorageRemovedEvent;
import cn.unikue.springstarter.filestorage.event.FileStorageUploadedEvent;
import cn.unikue.springstarter.filestorage.exception.FileStorageException;
import cn.unikue.springstarter.filestorage.property.TencentFileStorageProperties;
import cn.unikue.springstarter.filestorage.util.FileObjectStorageUtils;
import cn.unikue.springstarter.filestorage.util.TencentCosConfigUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


/**
 * File storage composer for tencent-cos
 *
 * @author David Hsing
 */
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class TencentFileStorageComposer implements FileStorageComposer, ApplicationEventPublisherAware, InitializingBean, DisposableBean {
    private final TencentFileStorageProperties properties;
    private final boolean concatDate;
    private final boolean publishEvent;
    private COSClient cosClient;

    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void afterPropertiesSet() throws Exception {
        cosClient = TencentCosConfigUtils.cosClient(properties);
    }

    @Override
    public boolean existsBucket() {
        if (StringUtils.isBlank(properties.getBucketName())) {
            return false;
        }
        try {
            return cosClient.doesBucketExist(properties.getBucketName());
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public boolean existsObject(@Nullable String objectKey, @Nullable String pathPrefix) {
        if (StringUtils.isAnyBlank(objectKey, properties.getBucketName())) {
            return false;
        }
        String objectPath = FileObjectStorageUtils.recurObjectPath(objectKey, pathPrefix);
        if (StringUtils.isBlank(objectPath)) {
            return false;
        }
        try {
            return cosClient.doesObjectExist(properties.getBucketName(), objectPath);
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
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(content.available());
            if (metadataProvider != null) {
                metadataProvider.accept(objectMetadata);
            }
            // Use objectPath instead of objectKey to avoid the problem of all objects are under the root bucket
            PutObjectRequest request = new PutObjectRequest(properties.getBucketName(), objectPath, content, objectMetadata);
            cosClient.putObject(request);
            if (publishEvent) {
                applicationEventPublisher.publishEvent(new FileStorageUploadedEvent(objectKey, pathPrefix, FileStorageType.TENCENT));
            }
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
            COSObject cosObject = cosClient.getObject(properties.getBucketName(), objectPath);
            return cosObject.getObjectContent();
        } catch (Exception ex) {
            throw new FileStorageException(ex);
        }
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void removeObject(@Nullable String objectKey, @Nullable String pathPrefix) throws FileStorageException {
        if (StringUtils.isAnyBlank(objectKey, properties.getBucketName())) {
            return;
        }
        String objectPath = FileObjectStorageUtils.recurObjectPath(objectKey, pathPrefix);
        if (StringUtils.isBlank(objectPath)) {
            return;
        }
        try {
            cosClient.deleteObject(properties.getBucketName(), objectPath);
            if (publishEvent) {
                applicationEventPublisher.publishEvent(new FileStorageRemovedEvent(objectKey, pathPrefix, FileStorageType.TENCENT));
            }
        } catch (Exception ex) {
            throw new FileStorageException(ex);
        }
    }

    @Override
    public String getObjectUrl(@Nullable String objectKey, @Nullable String pathPrefix, @Nullable Duration expiration) {
        if (StringUtils.isAnyBlank(objectKey, properties.getBucketName())) {
            return null;
        }
        String objectPath = FileObjectStorageUtils.recurObjectPath(objectKey, pathPrefix);
        if (StringUtils.isBlank(objectPath)) {
            return null;
        }
        if (DurationUtilsWraps.isNotPositive(expiration)) {
            return FileObjectStorageUtils.buildObjectUrl(FileStorageType.TENCENT, objectPath, properties.getDomain(), properties.getEndpoint(), properties.getBucketName(), BooleanUtils.isTrue(properties.getSslEnabled()));
        }
        Date expiryDate = JdkDateWraps.plusTemporal(JdkDateWraps.getCurrentDateTime(), expiration);
        try {
            return cosClient.generatePresignedUrl(properties.getBucketName(), objectPath, expiryDate).toString();
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public Object getRawClient() {
        return cosClient;
    }

    @Nonnull
    @Override
    public FileStorageType getStorageType() {
        return FileStorageType.TENCENT;
    }

    @Override
    public void destroy() {
        if (cosClient != null) {
            try {
                cosClient.shutdown();
            } catch (Exception ignored) {
            }
        }
    }
}
