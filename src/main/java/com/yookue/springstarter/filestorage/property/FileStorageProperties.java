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
import org.springframework.boot.context.properties.ConfigurationProperties;
import com.yookue.springstarter.filestorage.config.FileStorageAutoConfiguration;
import com.yookue.springstarter.filestorage.enumeration.FileStorageType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Properties for file storage
 *
 * @author David Hsing
 */
@ConfigurationProperties(prefix = FileStorageAutoConfiguration.PROPERTIES_PREFIX)
@Getter
@Setter
@ToString
public class FileStorageProperties implements Serializable {
    /**
     * Indicates whether to enable this starter or not
     * <p>
     * Default is {@code true}
     */
    private Boolean enabled = true;

    /**
     * Indicates whether to prepend date before the file key
     * <p>
     * Default is {@code true}
     */
    private Boolean concatDate = true;

    /**
     * The storage type pointer to the corresponding properties
     */
    private FileStorageType storageType;

    /**
     * The properties of local storage
     */
    private LocalFileStorageProperties local = new LocalFileStorageProperties();

    /**
     * The properties of minio storage
     */
    private MinioFileStorageProperties minio = new MinioFileStorageProperties();

    /**
     * The properties of aliyun-oss storage
     */
    private AliyunFileStorageProperties aliyun = new AliyunFileStorageProperties();

    /**
     * The properties of tencent-cos storage
     */
    private TencentFileStorageProperties tencent = new TencentFileStorageProperties();
}
