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
