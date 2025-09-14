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

package cn.unikue.springstarter.filestorage;


import java.io.File;
import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import cn.unikue.commonplexus.javaseutil.util.StackTraceWraps;
import cn.unikue.springstarter.filestorage.composer.FileStorageComposer;
import lombok.extern.slf4j.Slf4j;


@SpringBootTest(classes = MockApplicationInitializer.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
@SuppressWarnings("LoggingSimilarMessage")
class MockApplicationTest {
    @Autowired
    private FileStorageComposer storageComposer;

    @Test
    void uploadFile() throws Exception {
        String methodName = StackTraceWraps.getExecutingMethodName();
        String result = storageComposer.uploadObject(new File("/Volumes/Workspace/README.md"));
        log.info("{}: Upload object key is '{}'", methodName, result);
        Assertions.assertNotNull(result);
    }

    @Test
    void uploadText() throws Exception {
        String methodName = StackTraceWraps.getExecutingMethodName();
        String result = storageComposer.uploadObject("cn.unikue.springstarter.filestorage", ".txt");
        log.info("{}: Upload object key is '{}'", methodName, result);
        Assertions.assertNotNull(result);
    }

    @Test
    void getObjectUrl() {
        String methodName = StackTraceWraps.getExecutingMethodName();
        String result = storageComposer.getObjectUrl("20250818-7b1815131ead4f958cb333221f5b05ec-txt", null, Duration.ofHours(1));
        log.info("{}: Uploaded object url is '{}'", methodName, result);
        Assertions.assertNotNull(result);
    }
}
