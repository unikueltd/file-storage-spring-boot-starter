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

package com.yookue.springstarter.filestorage.util;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.model.Callback;
import com.aliyun.oss.model.CannedAccessControlList;
import com.yookue.commonplexus.javaseutil.util.ObjectUtilsWraps;
import com.yookue.commonplexus.javaseutil.util.StringUtilsWraps;
import com.yookue.springstarter.filestorage.property.AliyunFileStorageProperties;


/**
 * Utilities for configuring aliyun-oss client
 *
 * @author David Hsing
 */
@SuppressWarnings("unused")
public abstract class AliyunOssConfigUtils {
    /**
     * Returns a configured OSS client instance from the given properties
     *
     * @param properties The aliyun OSS storage properties
     *
     * @return a configured OSS client instance from the given properties
     *
     * @throws IllegalArgumentException if required properties are missing
     */
    @Nonnull
    public static OSS ossClient(@Nonnull AliyunFileStorageProperties properties) {
        if (StringUtils.isBlank(properties.getEndpoint())) {
            throw new IllegalArgumentException("Endpoint is required for OSS client");
        }
        if (StringUtils.isBlank(properties.getAccessKeyId())) {
            throw new IllegalArgumentException("AccessKeyId is required for OSS client");
        }
        if (StringUtils.isBlank(properties.getAccessKeySecret())) {
            throw new IllegalArgumentException("AccessKeySecret is required for OSS client");
        }
        CredentialsProvider provider = new DefaultCredentialProvider(properties.getAccessKeyId(), properties.getAccessKeySecret());
        ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
        ObjectUtilsWraps.ifNotNull(properties.getConnectionTimeout(), item -> configuration.setConnectionTimeout((int) item.toMillis()));
        ObjectUtilsWraps.ifNotNull(properties.getSocketTimeout(), item -> configuration.setSocketTimeout((int) item.toMillis()));
        ObjectUtilsWraps.ifNotNull(properties.getMaxConnections(), configuration::setMaxConnections);
        configuration.setProtocol(BooleanUtils.isTrue(properties.getSecureHttp()) ? Protocol.HTTPS : Protocol.HTTP);
        if (StringUtils.isNotBlank(properties.getProxyHost())) {
            configuration.setProxyHost(properties.getProxyHost());
            ObjectUtilsWraps.ifNotNull(properties.getProxyPort(), configuration::setProxyPort);
            if (StringUtils.isNotBlank(properties.getProxyUsername())) {
                configuration.setProxyUsername(properties.getProxyUsername());
                configuration.setProxyPassword(properties.getProxyPassword());
            }
        }
        OSS ossClient = new OSSClientBuilder().build(properties.getEndpoint(), provider, configuration);
        if (StringUtils.isNotBlank(properties.getBucketName())) {
            if (BooleanUtils.isTrue(properties.getAutoCreateBucket()) && !ossClient.doesBucketExist(properties.getBucketName())) {
                ossClient.createBucket(properties.getBucketName());
            }
            if (properties.getAccessControl() != null && properties.getAccessControl() != CannedAccessControlList.Unknown) {
                ossClient.setBucketAcl(properties.getBucketName(), properties.getAccessControl());
            }
        }
        return ossClient;
    }

    /**
     * Returns a callback instance from the given properties
     *
     * @param properties The aliyun OSS storage properties
     *
     * @return a callback instance from the given properties
     */
    @Nullable
    public static Callback callback(@Nonnull AliyunFileStorageProperties properties) {
        if (StringUtils.isBlank(properties.getCallbackUrl())) {
            return null;
        }
        Callback callback = new Callback();
        callback.setCallbackUrl(properties.getCallbackUrl());
        StringUtilsWraps.ifNotBlank(properties.getCallbackHost(), callback::setCallbackHost);
        StringUtilsWraps.ifNotBlank(properties.getCallbackBody(), callback::setCallbackBody);
        ObjectUtilsWraps.ifNotNull(properties.getCallbackBodyType(), callback::setCalbackBodyType);
        return callback;
    }
}
