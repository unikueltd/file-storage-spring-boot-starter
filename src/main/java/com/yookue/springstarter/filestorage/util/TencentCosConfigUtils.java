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
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import com.yookue.commonplexus.javaseutil.util.ObjectUtilsWraps;
import com.yookue.commonplexus.javaseutil.util.StringUtilsWraps;
import com.yookue.springstarter.filestorage.property.TencentFileStorageProperties;


/**
 * Utilities for configuring tencent-cos client
 *
 * @author David Hsing
 */
@SuppressWarnings("unused")
public abstract class TencentCosConfigUtils {
    /**
     * Returns a configured COS client instance from the given properties
     *
     * @param properties the Tencent COS storage properties
     *
     * @return a configured COS client instance from the given properties
     *
     * @throws IllegalArgumentException if required properties are missing
     * @throws CosClientException if failed to create COS client
     */
    @Nonnull
    @SuppressWarnings("RedundantThrows")
    public static COSClient cosClient(@Nonnull TencentFileStorageProperties properties) throws Exception {
        if (StringUtils.isBlank(properties.getSecretId())) {
            throw new IllegalArgumentException("SecretId is required for COS client");
        }
        if (StringUtils.isBlank(properties.getSecretKey())) {
            throw new IllegalArgumentException("SecretKey is required for COS client");
        }
        if (StringUtils.isBlank(properties.getRegion())) {
            throw new IllegalArgumentException("Region is required for COS client");
        }
        COSCredentials credentials = new BasicCOSCredentials(properties.getSecretId(), properties.getSecretKey());
        Region region = new Region(properties.getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(BooleanUtils.isTrue(properties.getSecureHttp()) ? HttpProtocol.https : HttpProtocol.http);
        ObjectUtilsWraps.ifNotNull(properties.getConnectionTimeout(), item -> clientConfig.setConnectionTimeout((int) item.toMillis()));
        ObjectUtilsWraps.ifNotNull(properties.getSocketTimeout(), item -> clientConfig.setSocketTimeout((int) item.toMillis()));
        ObjectUtilsWraps.ifNotNull(properties.getMaxConnections(), clientConfig::setMaxConnectionsCount);
        ObjectUtilsWraps.ifNotNull(properties.getConnectionRequestTimeout(), item -> clientConfig.setConnectionRequestTimeout((int) item.toMillis()));
        if (StringUtils.isNotBlank(properties.getProxyHost())) {
            clientConfig.setHttpProxyIp(properties.getProxyHost());
            clientConfig.setHttpProxyPort(properties.getProxyPort());
            if (StringUtils.isNotBlank(properties.getProxyUsername())) {
                clientConfig.setProxyUsername(properties.getProxyUsername());
                clientConfig.setProxyPassword(properties.getProxyPassword());
            }
        }
        StringUtilsWraps.ifNotBlank(properties.getUserAgent(), clientConfig::setUserAgent);
        ObjectUtilsWraps.ifNotNull(properties.getSignExpired(), item -> clientConfig.setSignExpired(item.getSeconds()));
        COSClient cosClient = new COSClient(credentials, clientConfig);
        if (StringUtils.isNotBlank(properties.getBucketName())) {
            if (BooleanUtils.isTrue(properties.getAutoCreateBucket()) && !cosClient.doesBucketExist(properties.getBucketName())) {
                cosClient.createBucket(properties.getBucketName());
            }
            if (properties.getAccessControl() != null) {
                cosClient.setBucketAcl(properties.getBucketName(), properties.getAccessControl());
            }
        }
        return cosClient;
    }
}
