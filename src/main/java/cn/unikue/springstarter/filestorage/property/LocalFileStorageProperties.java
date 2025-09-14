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

package cn.unikue.springstarter.filestorage.property;


import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Properties for local storage
 *
 * @author David Hsing
 */
@Getter
@Setter
@ToString
public class LocalFileStorageProperties implements Serializable {
    /**
     * The entry path of disk
     * <p>
     * For example, {@code "/Volumes/Workspace/storage"}, {@code "D:\\storage"}
     */
    private String entryPath;

    /**
     * The domain of access url
     * <p>
     * For example, {@code "http://localehost:9000"}
     */
    private String domain;
}
