package ru.kuznetsov.shop.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.kuznetsov.shop.represent.dto.ProductDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(value = {"/sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {"/sql/clean_up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class ProductControllerTest {

    private final static String API_PATH = "/product";
    private static final String SCHEME = "product";

    @Autowired
    protected EntityManagerFactory entityManagerFactory;

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper om;

    @Test
    @Order(1)
    void getById() throws Exception {
        int id = 1;

        MvcResult mvcResult = sendRequest(HttpMethod.GET, API_PATH + "/" + id, null)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ProductDto foundItem = om.readValue(json, ProductDto.class);

        assertEquals(id, foundItem.getId());
    }

    @Test
    @Order(2)
    void getAll() throws Exception {
        int count = getItemCount();

        MvcResult mvcResult = sendRequest(HttpMethod.GET, API_PATH, null)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ProductDto[] foundItem = om.readValue(json, ProductDto[].class);

        assertEquals(count, foundItem.length);
    }

    @Test
    @Order(3)
    void createAndReturn200() throws Exception {
        ProductDto mockDto = getMockDto();

        MvcResult mvcResult = sendRequest(HttpMethod.POST, API_PATH, mockDto)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        assertDoesNotThrow(() -> UUID.fromString(contentAsString));
    }

    @Test
    @Order(3)
    void createBatchAndReturn200() throws Exception {
        int addedCount = 4;
        List<ProductDto> dtos = new ArrayList<>();

        for (int i = 0; i < addedCount; i++) {
            dtos.add(getMockDto());
        }

        MvcResult mvcResult = sendRequest(HttpMethod.POST, API_PATH + "/batch", dtos)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        assertDoesNotThrow(() -> UUID.fromString(contentAsString));
    }

    @Test
    void delete() throws Exception {
        MvcResult mvcResult = sendRequest(HttpMethod.GET, API_PATH, null)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ProductDto[] foundItem = om.readValue(json, ProductDto[].class);

        assertNotEquals(0, foundItem.length);

        Long id = foundItem[0].getId();
        sendRequest(HttpMethod.DELETE, API_PATH + "/" + id, null);

        assertThrows(Exception.class, () -> sendRequest(HttpMethod.GET, API_PATH + "/" + id, null));
    }

    private ProductDto getMockDto() {
        ProductDto dto = new ProductDto();
        dto.setName("Test");
        dto.setDescription("Test");
        dto.setPrice(123);

        return dto;
    }

    private ResultActions sendRequest(HttpMethod httpMethod, String apiPath, Object body) throws Exception {
        return mockMvc.perform(request(httpMethod, apiPath)
                .content(om.writeValueAsString(body))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
    }

    protected Integer getItemCount() {
        Integer deviceCount;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            deviceCount = (Integer) entityManager
                    .createNativeQuery("select count(*) from " + SCHEME, Integer.class)
                    .getSingleResult();
        } catch (Exception e) {
            deviceCount = 0;
        }
        return deviceCount;
    }
}