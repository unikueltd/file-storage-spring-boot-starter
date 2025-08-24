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
import java.time.temporal.ChronoUnit;
import org.springframework.boot.convert.DurationUnit;
import com.qcloud.cos.model.CannedAccessControlList;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Properties for tencent-cos storage
 *
 * @author David Hsing
 */
@Getter
@Setter
@ToString
public class TencentFileStorageProperties implements Serializable {
    /**
     * Indicates whether to automatically create bucket if not exists
     * <p>
     * Default is {@code true}
     */
    private Boolean autoCreateBucket = true;

    /**
     * The endpoint for COS service
     * <p>
     * For example: {@code "https://cos.ap-beijing.myqcloud.com"} or {@code "cos.ap-beijing.myqcloud.com"}
     */
    private String endpoint;

    /**
     * SecretId for authentication
     */
    private String secretId;

    /**
     * SecretKey for authentication
     */
    private String secretKey;

    /**
     * The region of the COS service
     * <p>
     * For example: "ap-beijing", "ap-shanghai"
     */
    private String region;

    /**
     * Bucket name
     */
    private String bucketName;

    /**
     * The domain of endpoint for accessing files
     * <p>
     * For example: {@code "https://cdn.example.com"}
     */
    private String domain;

    /**
     * The canned access control for accessing files
     * <p>
     * For example: "private", "public-read", "public-read-write"
     */
    private CannedAccessControlList accessControl;

    /**
     * Indicates the protocol to access endpoint, using HTTPS or HTTP
     * <p>
     * Default is {@code true}
     */
    private Boolean sslEnabled = true;

    /**
     * Connection timeout
     * <p>
     * Default is 30 seconds
     */
    @DurationUnit(value = ChronoUnit.SECONDS)
    private Duration connectionTimeout = Duration.ofSeconds(30);

    /**
     * Socket timeout
     * <p>
     * Default is 30 seconds
     */
    @DurationUnit(value = ChronoUnit.SECONDS)
    private Duration socketTimeout = Duration.ofSeconds(30);

    /**
     * Maximum connections
     * <p>
     * Default is 1024
     */
    private Integer maxConnections = 1024;

    /**
     * Proxy host
     */
    private String proxyHost;

    /**
     * Proxy port
     */
    private Integer proxyPort;

    /**
     * Proxy username
     */
    private String proxyUsername;

    /**
     * Proxy password
     */
    private String proxyPassword;

    /**
     * User agent
     */
    private String userAgent;

    /**
     * The sign expired time
     * <p>
     * Default is 1 hour
     */
    @DurationUnit(value = ChronoUnit.HOURS)
    private Duration signExpired = Duration.ofHours(1);

    /**
     * The connection request timeout
     * <p>
     * Default is 30 seconds
     */
    @DurationUnit(value = ChronoUnit.SECONDS)
    private Duration connectionRequestTimeout = Duration.ofSeconds(30);
}
