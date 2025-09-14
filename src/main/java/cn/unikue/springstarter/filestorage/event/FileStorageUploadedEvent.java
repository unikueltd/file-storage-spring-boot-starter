/*
 * Copyright (c) 2016 Unikue Ltd. All rights reserved.
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

package cn.unikue.springstarter.filestorage.event;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.context.ApplicationEvent;
import cn.unikue.commonplexus.javaseutil.util.ObjectUtilsWraps;
import cn.unikue.springstarter.filestorage.enumeration.FileStorageType;
import lombok.Getter;


/**
 * Event when storage object uploaded
 *
 * @author David Hsing
 */
@Getter
@SuppressWarnings("unused")
public class FileStorageUploadedEvent extends ApplicationEvent {
    private final String pathPrefix;
    private final FileStorageType storageType;

    public FileStorageUploadedEvent(@Nonnull String objectKey, @Nullable String pathPrefix, @Nonnull FileStorageType storageType) {
        super(objectKey);
        this.pathPrefix = pathPrefix;
        this.storageType = storageType;
    }

    @Nonnull
    public String getObjectKey() {
        return ObjectUtilsWraps.castAsString(super.getSource());
    }
}
