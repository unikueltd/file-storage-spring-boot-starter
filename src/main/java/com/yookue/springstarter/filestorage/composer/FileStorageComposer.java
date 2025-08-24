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

package com.yookue.springstarter.filestorage.composer;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Consumer;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import com.yookue.commonplexus.javaseutil.util.ObjectUtilsWraps;
import com.yookue.springstarter.filestorage.enumeration.FileStorageType;
import com.yookue.springstarter.filestorage.exception.FileStorageException;


/**
 * General file storage composer interface
 *
 * @author David Hsing
 */
@SuppressWarnings("unused")
public interface FileStorageComposer {
    /**
     * Returns whether the bucket exists
     *
     * @return whether the bucket exists
     */
    default boolean existsBucket() {
        return false;
    }

    /**
     * Returns whether the object exists
     *
     * @param objectKey The object key identifier
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     *
     * @return whether the object exists
     */
    default boolean existsObject(@Nullable String objectKey, @Nullable String pathPrefix) {
        return false;
    }

    default String uploadObject(@Nullable byte[] content, @Nullable String extension) throws FileStorageException {
        return uploadObject(content, extension, null, null);
    }

    default String uploadObject(@Nullable byte[] content, @Nullable String extension, @Nullable String pathPrefix) throws FileStorageException {
        return uploadObject(content, extension, pathPrefix, null);
    }

    /**
     * Returns an object key by uploading content
     *
     * @param content The object bytes content
     * @param extension The file extension, will be lowercased
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     * @param metadataProvider The metadata provider for prepared put request, the source depends on the storage type, can be null
     *
     * @return an object key by uploading content
     */
    default String uploadObject(@Nullable byte[] content, @Nullable String extension, @Nullable String pathPrefix, @Nullable Consumer<Object> metadataProvider) throws FileStorageException {
        if (content == null) {
            return null;
        }
        try (ByteArrayInputStream stream = new ByteArrayInputStream(content)) {
            return uploadObject(stream, extension, pathPrefix, metadataProvider);
        } catch (Exception ex) {
            throw new FileStorageException(ex);
        }
    }

    default String uploadObject(@Nullable File content) throws FileStorageException {
        return uploadObject(content, null, null, null);
    }

    default String uploadObject(@Nullable File content, @Nullable String extension) throws FileStorageException {
        return uploadObject(content, extension, null, null);
    }

    default String uploadObject(@Nullable File content, @Nullable String extension, @Nullable String pathPrefix) throws FileStorageException {
        return uploadObject(content, extension, pathPrefix, null);
    }

    /**
     * Returns an object key by uploading content
     *
     * @param content The object file content
     * @param extension The file extension, will be lowercased, {@code null} means the same extension as the source file
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     * @param metadataProvider The metadata provider for prepared put request, the source depends on the storage type, can be null
     *
     * @return an object key by uploading content
     */
    default String uploadObject(@Nullable File content, @Nullable String extension, @Nullable String pathPrefix, @Nullable Consumer<Object> metadataProvider) throws FileStorageException {
        if (content == null) {
            return null;
        }
        try (FileInputStream stream = new FileInputStream(content)) {
            return uploadObject(stream, ObjectUtils.defaultIfNull(extension, FilenameUtils.getExtension(content.getName())), pathPrefix, metadataProvider);
        } catch (Exception ex) {
            throw new FileStorageException(ex);
        }
    }

    default String uploadObject(@Nullable String content, @Nullable String extension) throws FileStorageException {
        return uploadObject(content, extension, null, null, null);
    }

    default String uploadObject(@Nullable String content, @Nullable String extension, @Nullable String pathPrefix) throws FileStorageException {
        return uploadObject(content, extension, pathPrefix, null, null);
    }

    default String uploadObject(@Nullable String content, @Nullable String extension, @Nullable String pathPrefix, @Nullable Consumer<Object> metadataProvider) throws FileStorageException {
        return uploadObject(content, extension, pathPrefix, null, null);
    }

    /**
     * Returns an object key by uploading content
     *
     * @param content The object string content, not the path of file
     * @param extension The file extension, will be lowercased
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     * @param metadataProvider The metadata provider for prepared put request, the source depends on the storage type, can be null
     * @param contentCharset The content charset, can be null
     *
     * @return an object key by uploading content
     */
    default String uploadObject(@Nullable String content, @Nullable String extension, @Nullable String pathPrefix, @Nullable Consumer<Object> metadataProvider, @Nullable Charset contentCharset) throws FileStorageException {
        if (content == null) {
            return null;
        }
        Charset charsetAlias = ObjectUtils.defaultIfNull(contentCharset, StandardCharsets.UTF_8);
        try (ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes(charsetAlias))) {
            return uploadObject(stream, extension, pathPrefix, metadataProvider);
        } catch (Exception ex) {
            throw new FileStorageException(ex);
        }
    }

    default String uploadObject(@Nullable InputStream content, @Nullable String extension) throws FileStorageException {
        return uploadObject(content, extension, null, null);
    }

    default String uploadObject(@Nullable InputStream content, @Nullable String extension, @Nullable String pathPrefix) throws FileStorageException {
        return uploadObject(content, extension, pathPrefix, null);
    }

    /**
     * Returns an object key by uploading an input stream
     *
     * @param content The object input stream content
     * @param extension The file extension, will be lowercased
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     * @param metadataProvider The metadata provider for prepared put request, the source depends on the storage type, can be null
     *
     * @return an object key by uploading an input stream
     */
    String uploadObject(@Nullable InputStream content, @Nullable String extension, @Nullable String pathPrefix, @Nullable Consumer<Object> metadataProvider) throws FileStorageException;

    /**
     * Returns an input stream by downloading an object
     *
     * @param objectKey The object key identifier
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     *
     * @return an input stream by downloading an object
     */
    InputStream downloadObject(@Nullable String objectKey, @Nullable String pathPrefix) throws FileStorageException;

    /**
     * Downloads a storage object to the given target file
     *
     * @param objectKey The object key identifier
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     * @param targetFile The target file to write to
     */
    @SuppressWarnings("DataFlowIssue")
    default void downloadObjectTo(@Nullable String objectKey, @Nullable String pathPrefix, @Nullable String targetFile) throws FileStorageException {
        if (StringUtils.isAnyBlank(objectKey, targetFile)) {
            return;
        }
        downloadObjectTo(objectKey, pathPrefix, new File(targetFile));
    }

    /**
     * Downloads a storage object to the given target file
     *
     * @param objectKey The object key identifier
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     * @param targetFile The target file to write to
     */
    default void downloadObjectTo(@Nullable String objectKey, @Nullable String pathPrefix, @Nullable File targetFile) throws FileStorageException {
        if (StringUtils.isBlank(objectKey) || targetFile == null || !targetFile.isFile() || !targetFile.canWrite()) {
            return;
        }
        try (InputStream inputStream = downloadObject(objectKey, pathPrefix)) {
            FileUtils.copyInputStreamToFile(inputStream, targetFile);
        } catch (Exception ex) {
            throw (ex instanceof FileStorageException alias) ? alias : new FileStorageException(ex);
        }
    }

    /**
     * Removes an object in the storage
     *
     * @param objectKey The object key identifier
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     */
    void removeObject(@Nullable String objectKey, @Nullable String pathPrefix) throws FileStorageException;

    /**
     * Returns the object access URL
     *
     * @param objectKey The object key identifier
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     *
     * @return the object access URL
     */
    default String getObjectUrl(@Nullable String objectKey, @Nullable String pathPrefix) {
        return getObjectUrl(objectKey, pathPrefix, null);
    }

    /**
     * Returns the object access URL
     *
     * @param objectKey The object key identifier
     * @param pathPrefix The prefix before the filename, usually the category or the owner, can be null
     * @param expiration The expiration for the generated url, if {@code null}, the permanent url will be used
     *
     * @return the object access URL
     */
    String getObjectUrl(@Nullable String objectKey, @Nullable String pathPrefix, @Nullable Duration expiration);

    /**
     * Returns the raw storage client
     *
     * @return the raw storage client
     */
    default Object getRawClient() {
        return null;
    }

    /**
     * Returns the raw storage client as an expected type
     *
     * @param expectType The expected type of the raw storage client
     *
     * @return the raw storage client as an expected type
     */
    default <T> T getRawClientAs(@Nullable Class<T> expectType) {
        return ObjectUtilsWraps.castAs(getRawClient(), expectType);
    }

    /**
     * Returns the current storage type
     *
     * @return the current storage type
     */
    @Nonnull
    FileStorageType getStorageType();
}
