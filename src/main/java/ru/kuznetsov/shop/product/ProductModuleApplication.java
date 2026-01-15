package ru.kuznetsov.shop.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import ru.kuznetsov.shop.data.config.SpringConfig;
import ru.kuznetsov.shop.kafka.config.KafkaConfig;
import ru.kuznetsov.shop.parameter.config.ParameterConfig;

@SpringBootApplication
@EnableDiscoveryClient
@Import({SpringConfig.class, ParameterConfig.class, KafkaConfig.class})
public class ProductModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductModuleApplication.class, args);
    }

}
