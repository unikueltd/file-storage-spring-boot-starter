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


import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDate;
import java.util.function.Consumer;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import com.yookue.commonplexus.javaseutil.constant.CharVariantConst;
import com.yookue.commonplexus.javaseutil.exception.FileStorageException;
import com.yookue.commonplexus.javaseutil.identity.JdkUuidGenerator;
import com.yookue.commonplexus.javaseutil.util.FileUtilsWraps;
import com.yookue.commonplexus.javaseutil.util.LocalDateWraps;
import com.yookue.commonplexus.javaseutil.util.StringUtilsWraps;
import com.yookue.springstarter.filestorage.composer.FileStorageComposer;
import com.yookue.springstarter.filestorage.enumeration.FileStorageType;
import com.yookue.springstarter.filestorage.property.LocalFileStorageProperties;
import com.yookue.springstarter.filestorage.util.FileObjectStorageUtils;
import lombok.RequiredArgsConstructor;


/**
 * File storage composer for local file
 *
 * @author David Hsing
 */
@RequiredArgsConstructor
@SuppressWarnings({"unused", "ClassCanBeRecord"})
public class LocalFileStorageComposer implements FileStorageComposer, InitializingBean {
    private final LocalFileStorageProperties properties;
    private final boolean concatDate;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isBlank(properties.getEntryPath())) {
            throw new FileStorageException("Locale file storage endpoint is missing");
        }
        if (!FileUtilsWraps.exists(properties.getEntryPath())) {
            try {
                Files.createDirectories(Paths.get(properties.getEntryPath()).normalize());
            } catch (Exception ex) {
                throw new FileStorageException("Locale file storage endpoint is invalid", ex);
            }
        }
    }

    @Override
    public boolean existsObject(@Nullable String objectKey, @Nullable String pathPrefix) {
        String objectPath = FileObjectStorageUtils.recurObjectPath(objectKey, pathPrefix);
        if (StringUtils.isBlank(objectPath)) {
            return false;
        }
        try {
            File file = Paths.get(properties.getEntryPath(), objectPath).normalize().toFile();
            return file.exists() && file.isFile();
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public String uploadObject(@Nullable InputStream content, @Nullable String extension, @Nullable String pathPrefix, @Nullable Consumer<Object> metadataProvider) throws FileStorageException {
        if (content == null) {
            return null;
        }
        String serialId = JdkUuidGenerator.getPopularId();
        LocalDate currentDate = LocalDateWraps.getCurrentDate();
        String objectKey = FileObjectStorageUtils.buildObjectKey(serialId, extension, concatDate, currentDate);
        String objectPath = FileObjectStorageUtils.buildObjectPath(serialId, extension, concatDate, currentDate, pathPrefix);
        try {
            Path target = Paths.get(properties.getEntryPath(), objectPath).normalize();
            FileUtils.forceMkdirParent(target.toFile());
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
            return objectKey;
        } catch (Exception ex) {
            throw new FileStorageException(ex);
        }
    }

    @Override
    public InputStream downloadObject(@Nullable String objectKey, @Nullable String pathPrefix) throws FileStorageException {
        String objectPath = FileObjectStorageUtils.recurObjectPath(objectKey, pathPrefix);
        if (StringUtils.isBlank(objectPath)) {
            return null;
        }
        try {
            Path fullPath = Paths.get(properties.getEntryPath(), objectPath).normalize();
            return Files.newInputStream(fullPath);
        } catch (Exception ex) {
            throw new FileStorageException(ex);
        }
    }

    @Override
    public void removeObject(@Nullable String objectKey, @Nullable String pathPrefix) throws FileStorageException {
        String objectPath = FileObjectStorageUtils.recurObjectPath(objectKey, pathPrefix);
        if (StringUtils.isBlank(objectPath)) {
            return;
        }
        try {
            Path fullPath = Paths.get(properties.getEntryPath(), objectPath).normalize();
            Files.deleteIfExists(fullPath);
        } catch (Exception ex) {
            throw new FileStorageException(ex);
        }
    }

    @Override
    public String getObjectUrl(@Nullable String objectKey, @Nullable String pathPrefix, @Nullable Duration expiration) {
        String objectPath = FileObjectStorageUtils.recurObjectPath(objectKey, pathPrefix);
        if (StringUtils.isBlank(objectPath)) {
            return null;
        }
        return StringUtilsWraps.joinOnce(CharVariantConst.SLASH, properties.getDomain(), objectPath);
    }

    @Nonnull
    @Override
    public FileStorageType getStorageType() {
        return FileStorageType.LOCAL;
    }
}
