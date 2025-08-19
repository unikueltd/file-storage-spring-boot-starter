# File Storage Spring Boot Starter

Spring Boot application integrates file-storage, support various storage engines.

## Quickstart

- Import dependencies

```xml
    <dependency>
        <groupId>com.yookue.springstarter</groupId>
        <artifactId>file-storage-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> By default, this starter will auto take effect, you can turn it off by `spring.file-storage.enabled = false`

- Configure Spring Boot `application.yml` with prefix `spring.file-storage`

```yml
spring:
    file-storage:
        concat-date: true
        storage-type: 'minio'
        local:
            entry-path: '/path/to/your/storage'
            domain: 'http://localhost:8090'
        minio:
            endpoint: 'localhost'
            port: 9000
            access-key: 'minio'
            secret-key: 'minio123'
            bucket-name: 'demo-bucket'
            access-control: 'public-read'
        aliyun:
            endpoint: 'https://oss-cn-beijing.aliyuncs.com'
            access-key: 'your-access-key'
            secret-key: 'your-secret-key'
            bucket-name: 'demo-bucket'
            access-control: 'public-read'
        tencent:
            endpoint: 'https://cos.ap-beijing.myqcloud.com'
            secret-id: 'your-secret-id'
            secret-key: 'your-secret-key'
            bucket-name: 'demo-bucket'
            access-control: 'public-read'
```

- Then, in your code, you can use `FileStorageComposer` to store and retrieve files

```java
@Service
public class DemoService {
    @Autowired
    private FileStorageComposer storageComposer;

    public void demoMethod() {
        // Upload a file, or a pure text, or a byte array, or an input stream
        storageComposer.uploadObject();

        // Check if exist an object
        storageComposer.existsObject(objectKey, pathPrefix);

        // Download an object as an input stream
        storageComposer.downloadObject(objectKey, pathPrefix);

        // Download an object as a file
        storageComposer.downloadObjectTo(objectKey, pathPrefix, targetFile);

        // Delete an object
        storageComposer.deleteObject(objectKey, pathPrefix);

        // Get the object URL
        storageComposer.getObjectUrl(objectKey, pathPrefix);
        storageComposer.getObjectUrl(objectKey, pathPrefix, expiration);
        
        // Or get the raw client for advanced usage
        storageComposer.getRawClient();
        storageComposer.getRawClientAs(expectType);
    }
}
```

## Document

- Github: https://github.com/yookue/file-storage-spring-boot-starter

## Requirement

- jdk 17+

## License

This project is under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

See the `NOTICE.txt` file for required notices and attributions.

## Donation

You like this package? Then [donate to us](https://yookue.com/donation) to support the development.

## Copyright

Beijing Yookue Network Technology Ltd.

## Website

- Yookue: https://yookue.com
