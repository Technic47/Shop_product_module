package ru.kuznetsov.shop.product.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kuznetsov.shop.data.model.util.RestPage;
import ru.kuznetsov.shop.represent.dto.ProductCardDto;
import ru.kuznetsov.shop.represent.dto.ProductDto;

import java.util.Collection;
import java.util.List;

public interface ProductControllerApi {

    @Operation(summary = "Поиск по id", description = "Получение сущности по id записи")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductDto.class)
                    ),
                    description = "Товар"
            ),
            @ApiResponse(responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Товар не найден")
    })
    ResponseEntity<ProductDto> getById(
            @Parameter(description = "Уникальный идентификатор товара для поиска", required = true,
                    schema = @Schema(
                            description = "Id товара",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @PathVariable Long id);

    @Operation(summary = "Получение всех сущностей", description = "Получение всех сущностей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductDto[].class)
                    ),
                    description = "Список товаров"
            ),
            @ApiResponse(
                    responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Товаров не найдено"
            )
    })
    ResponseEntity<List<ProductDto>> getAll(
            @Parameter(description = "Уникальный идентификатор владельца товара для поиска",
                    schema = @Schema(
                            description = "Id владельца (uuid)",
                            example = "95381fbe-b068-4e88-abf5-85e96f64f507"
                    )
            )
            @RequestParam(value = "ownerId", required = false) String ownerId,
            @Parameter(description = "Уникальный идентификатор категории товаров для поиска",
                    schema = @Schema(
                            description = "Id категории",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @RequestParam(value = "categoryId", required = false) Long categoryId
    );

    @Operation(summary = "Получение карточек товаров", description = "Получение карточек товаров")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductCardDto[].class)
                    ),
                    description = "Список карточек товаров"
            ),
            @ApiResponse(
                    responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Карточек товаров не найдено"
            )
    })
    ResponseEntity<Collection<ProductCardDto>> getAllCard(
            @Parameter(description = "Уникальный идентификатор категории товаров для поиска",
                    schema = @Schema(
                            description = "Id категории",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Уникальный идентификатор владельца товара для поиска",
                    schema = @Schema(
                            description = "Id владельца (uuid)",
                            example = "95381fbe-b068-4e88-abf5-85e96f64f507"
                    )
            )
            @RequestParam(required = false) String ownerId
    );

    @Operation(summary = "Получение карточек товаров постранично", description = "Получение карточек товаров постранично")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestPage.class)
                    ),
                    description = "Страница с карточами товаров"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Не корректно указаны данные"
            )
    })
    ResponseEntity<RestPage<ProductCardDto>> getAllCardPageable(
            @Parameter(description = "Номер страницы", required = true,
                    schema = @Schema(
                            description = "Номер страницы",
                            example = "123",
                            type = "integer"
                    )
            )
            @RequestParam Integer pageNumber,
            @Parameter(description = "Размер страницы",
                    schema = @Schema(
                            description = "Размер страницы",
                            example = "123",
                            type = "integer"
                    )
            )
            @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "Направление сортировки",
                    schema = @Schema(
                            description = "Направление сортировки",
                            example = "ASC, DESC"
                    )
            )
            @RequestParam(required = false) String order,
            @Parameter(description = "Поля для сортировки",
                    schema = @Schema(
                            description = "Поля для сортировки",
                            example = "price"
                    )
            )
            @RequestParam(required = false) String[] sortBy,
            @Parameter(description = "Уникальный идентификатор категории товаров для поиска",
                    schema = @Schema(
                            description = "Id категории",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Уникальный идентификатор владельца товара для поиска",
                    schema = @Schema(
                            description = "Id владельца (uuid)",
                            example = "95381fbe-b068-4e88-abf5-85e96f64f507"
                    )
            )
            @RequestParam(required = false) String ownerId
    );

    @Operation(summary = "Создание товара", description = "Создание товара")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = String.class,
                                    description = "Номер операции по сохранению сущности"
                            )
                    ),
                    description = "Сообщение о создании сущности отправлено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Не корректно указаны данные"
            )
    })
    ResponseEntity<String> create(
            @Parameter(description = "Модель товара для создания", required = true,
                    schema = @Schema(
                            implementation = ProductDto.class,
                            description = "Товар"
                    ))
            @RequestBody ProductDto productDto);

    @Operation(summary = "Создание нескольких товара", description = "Создание нескольких товара")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = String.class,
                                    description = "Номер операции по сохранению нескольких сущностей"
                            )
                    ),
                    description = "Сообщение о создании нескольких сущностей отправлено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Не корректно указаны данные"
            )
    })
    ResponseEntity<String> createBatch(
            @Parameter(description = "Модель товара для создания", required = true,
                    schema = @Schema(
                            implementation = ProductDto[].class,
                            description = "Товар"
                    ))
            @RequestBody Collection<ProductDto> productDtoCollection);

    @Operation(summary = "Удаление по id", description = "Удаление сущности по id записи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар удалён"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    void deleteStore(
            @Parameter(description = "Уникальный идентификатор товара для удаления", required = true,
                    schema = @Schema(
                            description = "Id товара",
                            example = "123",
                            type = "integer",
                            format = "int64"
                    )
            )
            @PathVariable Long id);
}
