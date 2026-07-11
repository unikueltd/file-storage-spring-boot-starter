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

package cn.unikue.springstarter.filestorage.util;


import java.time.LocalDate;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import cn.unikue.commonplexus.javaseutil.constant.CharVariantConst;
import cn.unikue.commonplexus.javaseutil.constant.TemporalFormatConst;
import cn.unikue.commonplexus.javaseutil.enumeration.InetProtocolType;
import cn.unikue.commonplexus.javaseutil.identity.JdkUuidGenerator;
import cn.unikue.commonplexus.javaseutil.util.ArrayUtilsWraps;
import cn.unikue.commonplexus.javaseutil.util.FilenamePlainWraps;
import cn.unikue.commonplexus.javaseutil.util.LocalDateWraps;
import cn.unikue.commonplexus.javaseutil.util.StringUtilsWraps;
import cn.unikue.springstarter.filestorage.enumeration.FileStorageType;


/**
 * Utilities for file object storage
 *
 * @author David Hsing
 */
@SuppressWarnings("unused")
public abstract class FileObjectStorageUtils {
    /**
     * Returns an object key for storage
     *
     * <pre>
     *     FileObjectStorageUtils.buildObjectKey("1234567890", "jpg", true, LocalDate.now()) = "20250816-1234567890-jpg";
     *     FileObjectStorageUtils.buildObjectKey("1234567890", ".gif", false, null) = "1234567890-gif";
     *     FileObjectStorageUtils.buildObjectKey("1234567890", null, false, null) = "1234567890";
     * </pre>
     *
     * @param serialId The serial id for the file, used for filename, usually uuid
     * @param extension The file extension, will be converted to lowercase
     * @param concatDate Whether to concatenate the date to the file key
     * @param date The date to be concatenated before the file key, when {@code concatDate} is {@code true}
     *
     * @return an object key for storage
     */
    @Nonnull
    public static String buildObjectKey(@Nullable String serialId, @Nullable String extension, boolean concatDate, @Nullable LocalDate date) {
        StringBuilder builder = new StringBuilder();
        if (concatDate) {
            builder.append(LocalDateWraps.formatDate(ObjectUtils.defaultIfNull(date, LocalDateWraps.getCurrentDate()), TemporalFormatConst.RAW_YYYYMMDD));
            builder.append(CharVariantConst.HYPHEN);
        }
        builder.append(StringUtils.defaultIfBlank(serialId, JdkUuidGenerator.getRandomId()));
        StringUtilsWraps.ifNotBlank(FilenamePlainWraps.removeExtensionDot(extension), item -> builder.append(CharVariantConst.HYPHEN).append(item.toLowerCase()));
        return builder.toString();
    }

    /**
     * Returns an object path for storage
     * <p>
     * Without the start slash
     *
     * <pre>
     *     FileObjectStorageUtils.buildObjectPath("1234567890", "jpg", true, LocalDate.now(), "/avatar") = "avatar/2025/08/16/1234567890.jpg";
     *     FileObjectStorageUtils.buildObjectPath("1234567890", ".gif", false, null, "avatar") = "avatar/1234567890.gif";
     *     FileObjectStorageUtils.buildObjectPath("1234567890", null, false, null, null) = "1234567890";
     * </pre>
     *
     * @param serialId The serial id for the file, used for filename, usually uuid
     * @param extension The file extension, will be converted to lowercase
     * @param concatDate Whether to concatenate the date to the file key
     * @param date The date to be concatenated before the file key, when {@code concatDate} is {@code true}
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     *
     * @return an object path for storage
     */
    @Nonnull
    public static String buildObjectPath(@Nullable String serialId, @Nullable String extension, boolean concatDate, @Nullable LocalDate date, @Nullable String pathPrefix) {
        String pathAlias = FilenameUtils.separatorsToUnix(FilenamePlainWraps.removeStartSlashes(pathPrefix));
        StringBuilder builder = new StringBuilder();
        StringUtilsWraps.ifNotBlank(FilenameUtils.normalizeNoEndSeparator(pathAlias, true), item -> builder.append(item).append(CharVariantConst.SLASH));
        if (concatDate) {
            builder.append(LocalDateWraps.formatDate(ObjectUtils.defaultIfNull(date, LocalDateWraps.getCurrentDate()), TemporalFormatConst.EUR_YYYYMMDD));
            builder.append(CharVariantConst.SLASH);
        }
        builder.append(StringUtils.defaultIfBlank(serialId, JdkUuidGenerator.getRandomId()));
        StringUtilsWraps.ifNotBlank(FilenamePlainWraps.removeExtensionDot(extension), item -> builder.append(CharVariantConst.DOT).append(item.toLowerCase()));
        return builder.toString();
    }

    /**
     * Returns the full permanent url for the given object path
     *
     * @param storageType The storage type identifier
     * @param objectPath The object path to inspect
     * @param domain The domain for the storage, grouped by "objectPath", "domain"
     * @param endpoint The endpoint for the storage, grouped by "objectPath", "endpoint", "bucket"
     * @param bucket The bucket name for the storage, grouped by "objectPath", "endpoint", "bucket"
     * @param secureHttp Whether to use https for the storage
     *
     * @return the full permanent url for the given object path
     */
    @Nullable
    public static String buildObjectUrl(@Nullable FileStorageType storageType, @Nullable String objectPath, @Nullable String domain, @Nullable String endpoint, @Nullable String bucket, boolean secureHttp) {
        String pathAlias = FilenameUtils.separatorsToUnix(FilenamePlainWraps.removeStartSlashes(objectPath));
        String protocol;
        if (StringUtils.isNotBlank(domain)) {
            protocol = (secureHttp || StringUtils.startsWithIgnoreCase(domain, InetProtocolType.HTTPS.getValueWithDelimiter())) ? InetProtocolType.HTTPS.getValue() : InetProtocolType.HTTP.getValue();
        } else {
            protocol = (secureHttp || StringUtils.startsWithIgnoreCase(endpoint, InetProtocolType.HTTPS.getValueWithDelimiter())) ? InetProtocolType.HTTPS.getValue() : InetProtocolType.HTTP.getValue();
        }
        domain = StringUtilsWraps.removeStartIgnoreCase(FilenamePlainWraps.removeEndSlashes(domain), InetProtocolType.HTTP.getValueWithDelimiter(), InetProtocolType.HTTPS.getValueWithDelimiter());
        endpoint = StringUtilsWraps.removeStartIgnoreCase(FilenamePlainWraps.removeEndSlashes(endpoint), InetProtocolType.HTTP.getValueWithDelimiter(), InetProtocolType.HTTPS.getValueWithDelimiter());
        if (StringUtils.isAnyBlank(protocol, bucket, pathAlias) || StringUtils.isAllBlank(domain, endpoint)) {
            return null;
        }
        if (storageType == FileStorageType.MINIO) {
            return String.format("%s://%s/%s/%s", protocol, StringUtils.defaultIfBlank(domain, endpoint), bucket, pathAlias);    // $NON-NLS-1$
        } else if (storageType == FileStorageType.ALIYUN || storageType == FileStorageType.TENCENT) {
            return String.format("%s://%s.%s/%s", protocol, bucket, StringUtils.defaultIfBlank(domain, endpoint), pathAlias);    // $NON-NLS-1$
        }
        return null;
    }

    @Nullable
    public static String recurObjectPath(@Nullable String objectKey) {
        return recurObjectPath(objectKey, null);
    }

    /**
     * Returns a reoccurred object path from the given file key
     * <p>
     * Without the start slash
     *
     * <pre>
     *     FileObjectStorageUtils.recurObjectPath("20250816-1234567890-jpg", "/avatar") = "avatar/2025/08/16/1234567890.jpg";
     *     FileObjectStorageUtils.recurObjectPath("1234567890-gif", "avatar") = "avatar/1234567890.gif";
     *     FileObjectStorageUtils.recurObjectPath("1234567890", null) = "1234567890";
     * </pre>
     *
     * @param objectKey The object key identifier
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     *
     * @return a reoccurred object path from the given file key
     */
    @Nullable
    public static String recurObjectPath(@Nullable String objectKey, @Nullable String pathPrefix) {
        if (StringUtils.isBlank(objectKey) || StringUtils.countMatches(objectKey, CharVariantConst.HYPHEN) > 2) {
            return null;
        }
        String pathAlias = FilenamePlainWraps.removeStartSlashes(pathPrefix);
        String[] array = StringUtils.split(objectKey, CharVariantConst.HYPHEN);
        StringBuilder builder = new StringBuilder();
        StringUtilsWraps.ifNotBlank(FilenameUtils.normalizeNoEndSeparator(pathAlias, true), item -> builder.append(item).append(CharVariantConst.SLASH));
        if (array.length == 1) {
            builder.append(objectKey);
            return builder.toString();
        }
        String extension = ArrayUtilsWraps.forEachIndexingTailing(array, (index, item) -> {
            if (index == 0 && array.length > 2) {
                LocalDate date = LocalDateWraps.parseDateQuietly(item, TemporalFormatConst.RAW_YYYYMMDD);
                builder.append((date == null) ? item : LocalDateWraps.formatDate(date, TemporalFormatConst.EUR_YYYYMMDD));
            } else {
                builder.append(item);
            }
            if (index < array.length - 2) {
                builder.append(CharVariantConst.SLASH);
            }
        });
        if (StringUtils.isNotBlank(extension)) {
            builder.append(CharVariantConst.DOT).append(extension.toLowerCase());
        }
        return builder.toString();
    }
}
