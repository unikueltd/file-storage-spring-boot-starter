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

package com.yookue.springstarter.filestorage.property;


import java.io.Serializable;
import java.time.Duration;
import com.aliyun.oss.model.Callback;
import com.aliyun.oss.model.CannedAccessControlList;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Properties for aliyun-oss storage
 *
 * @author David Hsing
 */
@Getter
@Setter
@ToString
public class AliyunFileStorageProperties implements Serializable {
    /**
     * Indicates whether to automatically create bucket if not exists
     * <p>
     * Default is {@code true}
     */
    private Boolean autoCreateBucket = true;

    /**
     * The endpoint of OSS service
     * <p>
     * For example: {@code "https://oss-cn-beijing.aliyuncs.com"} or {@code "oss-cn-beijing.aliyuncs.com"}
     */
    private String endpoint;

    /**
     * The access key ID for authentication
     */
    private String accessKeyId;

    /**
     * The access key secret for authentication
     */
    private String accessKeySecret;

    /**
     * The region of the OSS service
     * <p>
     * For example: "cn-hangzhou"
     */
    private String region;

    /**
     * The name of the bucket to store files
     */
    private String bucketName;

    /**
     * The custom domain for accessing files
     * <p>
     * If not specified, the default OSS domain will be used
     */
    private String domain;

    /**
     * The canned access control for accessing files
     * <p>
     * For example: "private", "public-read", "public-read-write"
     */
    private CannedAccessControlList accessControl;

    /**
     * The connection timeout
     * <p>
     * Default is 50 seconds
     */
    private Duration connectionTimeout = Duration.ofSeconds(50);

    /**
     * The socket timeout
     * <p>
     * Default is 50 seconds
     */
    private Duration socketTimeout = Duration.ofSeconds(50);

    /**
     * The maximum connections allowed
     * <p>
     * Default is 1000
     */
    private Integer maxConnections = 1000;

    /**
     * Indicates the protocol to access endpoint, use HTTPS or HTTP
     * <p>
     * Default is {@code true}
     */
    private Boolean secureHttp = true;

    /**
     * The proxy host for OSS client
     */
    private String proxyHost;

    /**
     * The proxy port for OSS client
     */
    private Integer proxyPort;

    /**
     * The proxy username for OSS client
     */
    private String proxyUsername;

    /**
     * The proxy password for OSS client
     */
    private String proxyPassword;

    /**
     * The callback URL for upload completion notification
     */
    private String callbackUrl;

    /**
     * The callback host for upload completion notification
     */
    private String callbackHost;

    /**
     * The callback body for upload completion notification
     * <p>
     * For example: {@code "key=$(key)&etag=$(etag)"}
     */
    private String callbackBody;

    /**
     * The callback body type for upload completion notification
     */
    private Callback.CalbackBodyType callbackBodyType;
}
