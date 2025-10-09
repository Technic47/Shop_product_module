package ru.kuznetsov.shop.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.shop.data.dto.ProductDto;
import ru.kuznetsov.shop.data.service.ProductService;
import ru.kuznetsov.shop.product.service.KafkaService;

import java.util.Collection;
import java.util.List;

import static ru.kuznetsov.shop.data.common.KafkaTopics.PRODUCT_SAVE_TOPIC;

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
    public ResponseEntity<Boolean> create(@RequestBody ProductDto storeDto) {
        return ResponseEntity.ok(kafkaService.sendSaveMessage(storeDto, PRODUCT_SAVE_TOPIC));
    }

    @PostMapping("/batch")
    public ResponseEntity<Collection<Boolean>> createBatch(@RequestBody Collection<ProductDto> storeDto) {
        return ResponseEntity.ok(
                storeDto.stream()
                        .map(dto -> kafkaService.sendSaveMessage(dto, PRODUCT_SAVE_TOPIC))
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteeStore(@PathVariable Long id) {
        productService.deleteById(id);
    }
}
