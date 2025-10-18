package ru.kuznetsov.shop.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.shop.data.service.KafkaService;
import ru.kuznetsov.shop.data.service.ProductService;
import ru.kuznetsov.shop.represent.dto.ProductDto;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static ru.kuznetsov.shop.represent.common.KafkaConst.OPERATION_ID_HEADER;
import static ru.kuznetsov.shop.represent.common.KafkaConst.PRODUCT_SAVE_TOPIC;


@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final KafkaService kafkaService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping()
    public ResponseEntity<List<ProductDto>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @PostMapping
    public ResponseEntity<Boolean> create(@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(kafkaService.sendMessageWithEntity(
                productDto,
                PRODUCT_SAVE_TOPIC,
                Collections.singletonMap(OPERATION_ID_HEADER, UUID.randomUUID().toString().getBytes())));
    }

    @PostMapping("/batch")
    public ResponseEntity<Collection<Boolean>> createBatch(@RequestBody Collection<ProductDto> productDtoCollection) {
        byte[] operationId = UUID.randomUUID().toString().getBytes();

        return ResponseEntity.ok(
                productDtoCollection.stream()
                        .map(dto -> kafkaService.sendMessageWithEntity(dto,
                                PRODUCT_SAVE_TOPIC,
                                Collections.singletonMap(OPERATION_ID_HEADER, operationId)))
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteeStore(@PathVariable Long id) {
        productService.deleteById(id);
    }
}
