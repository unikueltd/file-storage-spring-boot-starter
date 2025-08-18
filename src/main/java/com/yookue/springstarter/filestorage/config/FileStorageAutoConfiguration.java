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

package com.yookue.springstarter.filestorage.config;


import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import com.yookue.commonplexus.springcondition.annotation.ConditionalOnAllProperties;
import com.yookue.springstarter.filestorage.composer.FileStorageComposer;
import com.yookue.springstarter.filestorage.composer.impl.AliyunFileStorageComposer;
import com.yookue.springstarter.filestorage.composer.impl.LocalFileStorageComposer;
import com.yookue.springstarter.filestorage.composer.impl.MinioFileStorageComposer;
import com.yookue.springstarter.filestorage.composer.impl.TencentFileStorageComposer;
import com.yookue.springstarter.filestorage.property.FileStorageProperties;


/**
 * Configuration for file storage
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBooleanProperty(prefix = FileStorageAutoConfiguration.PROPERTIES_PREFIX, name = "enabled", matchIfMissing = true)
@Import(value = {FileStorageAutoConfiguration.Entry.class, FileStorageAutoConfiguration.Stage.class})
public class FileStorageAutoConfiguration {
    public static final String PROPERTIES_PREFIX = "spring.file-storage";    // $NON-NLS-1$


    @Order(value = 0)
    @Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
    static class Entry {
        @Bean
        @ConditionalOnMissingBean
        public FileStorageProperties fileStorageProperties() {
            return new FileStorageProperties();
        }
    }


    @Order(value = 1)
    @Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
    static class Stage {
        @Bean
        @ConditionalOnAllProperties(value = {
            @ConditionalOnProperty(prefix = FileStorageAutoConfiguration.PROPERTIES_PREFIX, name = "storage-type", havingValue = "local"),
            @ConditionalOnProperty(prefix = FileStorageAutoConfiguration.PROPERTIES_PREFIX + ".local", name = "entry-path")
        })
        @ConditionalOnMissingBean
        public FileStorageComposer localFileStorageComposer(@Nonnull FileStorageProperties properties) {
            return new LocalFileStorageComposer(properties.getLocal(), BooleanUtils.isTrue(properties.getConcatDate()));
        }

        @Bean
        @ConditionalOnAllProperties(value = {
            @ConditionalOnProperty(prefix = FileStorageAutoConfiguration.PROPERTIES_PREFIX, name = "storage-type", havingValue = "minio"),
            @ConditionalOnProperty(prefix = FileStorageAutoConfiguration.PROPERTIES_PREFIX + ".minio", name = "endpoint")
        })
        @ConditionalOnClass(value = io.minio.MinioClient.class)
        @ConditionalOnMissingBean
        public FileStorageComposer minioFileStorageComposer(@Nonnull FileStorageProperties properties) {
            return new MinioFileStorageComposer(properties.getMinio(), BooleanUtils.isTrue(properties.getConcatDate()));
        }

        @Bean
        @ConditionalOnAllProperties(value = {
            @ConditionalOnProperty(prefix = FileStorageAutoConfiguration.PROPERTIES_PREFIX, name = "storage-type", havingValue = "aliyun"),
            @ConditionalOnProperty(prefix = FileStorageAutoConfiguration.PROPERTIES_PREFIX + ".aliyun", name = "endpoint")
        })
        @ConditionalOnClass(value = com.aliyun.oss.OSS.class)
        @ConditionalOnMissingBean
        public FileStorageComposer aliyunFileStorageComposer(@Nonnull FileStorageProperties properties) {
            return new AliyunFileStorageComposer(properties.getAliyun(), BooleanUtils.isTrue(properties.getConcatDate()));
        }

        @Bean
        @ConditionalOnAllProperties(value = {
            @ConditionalOnProperty(prefix = FileStorageAutoConfiguration.PROPERTIES_PREFIX, name = "storage-type", havingValue = "tencent"),
            @ConditionalOnProperty(prefix = FileStorageAutoConfiguration.PROPERTIES_PREFIX + ".tencent", name = "endpoint")
        })
        @ConditionalOnClass(value = com.qcloud.cos.COSClient.class)
        @ConditionalOnMissingBean
        public FileStorageComposer tencentFileStorageComposer(@Nonnull FileStorageProperties properties) {
            return new TencentFileStorageComposer(properties.getTencent(), BooleanUtils.isTrue(properties.getConcatDate()));
        }
    }
}
