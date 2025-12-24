package ru.kuznetsov.shop.product.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.shop.data.service.ProductPagingAndSortingService;
import ru.kuznetsov.shop.data.service.ProductService;
import ru.kuznetsov.shop.kafka.service.KafkaService;
import ru.kuznetsov.shop.represent.dto.ProductCardDto;
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

    private static final Integer DEFAULT_PAGE_SIZE = 10;

    private final ProductService productService;
    private final ProductPagingAndSortingService pagingAndSortingService;
    private final KafkaService kafkaService;

    Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping()
    public ResponseEntity<List<ProductDto>> getAll(
            @RequestParam(value = "ownerId", required = false) String ownerId,
            @RequestParam(value = "categoryId", required = false) Long categoryId
    ) {
        if (ownerId != null && !ownerId.isEmpty()) {
            return ResponseEntity.ok(productService.findAllByOwnerOrCategoryId(UUID.fromString(ownerId), categoryId));
        } else return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/card")
    public ResponseEntity<Collection<ProductCardDto>> getAllCard(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String ownerId
    ) {
        return ResponseEntity.ok(pagingAndSortingService.findAllByCategoryOrOwnerId(
                ownerId == null ? null : UUID.fromString(ownerId),
                categoryId
        ));
    }

    @GetMapping("/card/page")
    public ResponseEntity<Page<ProductCardDto>> getAllCardPageable(
            @RequestParam Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String[] sortBy,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String ownerId
    ) {
        Pageable pageable = getPageable(pageNumber, pageSize, order, sortBy);
        if (categoryId != null || ownerId != null) {
            return ResponseEntity.ok(pagingAndSortingService.findAllByCategoryOrOwnerIdPageable(
                    ownerId == null ? null : UUID.fromString(ownerId),
                    categoryId,
                    pageable
            ));
        } else
            return ResponseEntity.ok(pagingAndSortingService.findAllPageable(pageable));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody ProductDto productDto) {
        String uuidString = UUID.randomUUID().toString();

        sendMessageToKafka(productDto, uuidString);

        return ResponseEntity.ok(uuidString);
    }

    @PostMapping("/batch")
    public ResponseEntity<String> createBatch(@RequestBody Collection<ProductDto> productDtoCollection) {
        String uuidString = UUID.randomUUID().toString();

        for (ProductDto productDto : productDtoCollection) {
            sendMessageToKafka(productDto, uuidString);
        }

        return ResponseEntity.ok(uuidString);
    }

    @DeleteMapping("/{id}")
    public void deleteStore(@PathVariable Long id) {
        productService.deleteById(id);
    }

    private void sendMessageToKafka(ProductDto productDto, String uuidString) {
        boolean sendResult = kafkaService.sendMessageWithEntity(
                productDto,
                PRODUCT_SAVE_TOPIC,
                Collections.singletonMap(OPERATION_ID_HEADER, uuidString.getBytes()));

        if (!sendResult) {
            logger.warn("Failed to send product to topic. Product: {} operation id {}", productDto, uuidString);
        }
    }

    private Pageable getPageable(
            Integer pageNumber,
            Integer pageSize,
            String order,
            String[] sortBy
    ) {
        pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;

        if (sortBy != null && sortBy.length > 0) {
            return PageRequest.of(
                    pageNumber,
                    pageSize,
                    Sort.by(Sort.Direction.fromString(
                            order != null && order.equalsIgnoreCase("ASC") ? "ASC" : "DESC"
                    ), sortBy)
            );
        } else return PageRequest.of(pageNumber, pageSize);
    }
}
