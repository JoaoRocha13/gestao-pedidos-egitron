package com.egitron.gestaopedidos.api.order;

import com.egitron.gestaopedidos.dto.request.CreateOrderDTO;
import com.egitron.gestaopedidos.exception.BadRequestException;
import com.egitron.gestaopedidos.service.ErrorLogService;
import com.egitron.gestaopedidos.service.OrderService;
import com.egitron.gestaopedidos.service.OrderStatusHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mvc;

    @Mock private OrderService orderService;
    @Mock private ErrorLogService errorLogService;
    @Mock private OrderStatusHistoryService orderStatusHistoryService;

    @InjectMocks private OrderController orderController;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setControllerAdvice(new TestExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    // GET /api/orders/boom  500
    @Test
    void boom_deveRetornar500() throws Exception {
        mvc.perform(get("/api/orders/boom"))
                .andExpect(status().isInternalServerError());
    }

    // POST /api/orders inválido 400
    @Test
    void criarPedido_invalido_deveRetornar400() throws Exception {
        when(orderService.create(any(CreateOrderDTO.class)))
                .thenThrow(new BadRequestException("dados inválidos"));

        String body = "{"
                + "\"clientName\":\"Rock Silva\","
                + "\"clientEmail\":\"rock@email.com\","
                + "\"amount\":120.50"
                + "}";

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<Void> handleBadRequest(BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Void> handleGeneric(Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
