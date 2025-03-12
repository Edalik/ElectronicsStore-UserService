package ru.edalik.electronics.store.user.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.edalik.electronics.store.user.service.model.dto.BalanceDto;
import ru.edalik.electronics.store.user.service.model.exception.InsufficientFunds;
import ru.edalik.electronics.store.user.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.user.service.service.interfaces.BalanceService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
class BalanceControllerTest {

    static final String BASE_URL = "/api/v1/users/balance";
    static final String USER_ID_HEADER = "User-Id";
    static final UUID USER_ID = UUID.randomUUID();
    static final BigDecimal AMOUNT = BigDecimal.ONE;
    static final String DEPOSIT = "/deposit";
    static final String PAYMENT = "/payment";
    static final String USER_NOT_FOUND = "User not found";
    static final String NOT_FOUND = "Not Found";


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BalanceService balanceService;

    @Test
    void getUserBalance_ExistingUser_ReturnsBalance() throws Exception {
        when(balanceService.getBalance(USER_ID)).thenReturn(AMOUNT);

        mockMvc.perform(get(BASE_URL)
                .header(USER_ID_HEADER, USER_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amount").value(AMOUNT));
    }

    @Test
    void getUserBalance_UserNotFound_ReturnsNotFound() throws Exception {
        when(balanceService.getBalance(USER_ID)).thenThrow(new NotFoundException(USER_NOT_FOUND));

        mockMvc.perform(get(BASE_URL)
                .header(USER_ID_HEADER, USER_ID))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(NOT_FOUND));
    }

    @Test
    void getUserBalance_MissingUserId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deposit_ValidRequest_ReturnsOk() throws Exception {
        BalanceDto dto = new BalanceDto(AMOUNT);

        mockMvc.perform(post(BASE_URL + DEPOSIT)
                .header(USER_ID_HEADER, USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());

        verify(balanceService).deposit(dto, USER_ID);
    }

    @Test
    void deposit_UserNotFound_ReturnsNotFound() throws Exception {
        BalanceDto dto = new BalanceDto(AMOUNT);
        doThrow(new NotFoundException(USER_NOT_FOUND)).when(balanceService).deposit(dto, USER_ID);

        mockMvc.perform(post(BASE_URL + DEPOSIT)
                .header(USER_ID_HEADER, USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(NOT_FOUND));
    }

    @Test
    void payment_SufficientFunds_ReturnsOk() throws Exception {
        BalanceDto dto = new BalanceDto(AMOUNT);

        mockMvc.perform(post(BASE_URL + PAYMENT)
                .header(USER_ID_HEADER, USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());

        verify(balanceService).payment(dto, USER_ID);
    }

    @Test
    void payment_InsufficientFunds_ReturnsBadRequest() throws Exception {
        BalanceDto dto = new BalanceDto(AMOUNT);
        doThrow(new InsufficientFunds())
            .when(balanceService).payment(dto, USER_ID);

        mockMvc.perform(post(BASE_URL + PAYMENT)
                .header(USER_ID_HEADER, USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    @Test
    void payment_UserNotFound_ReturnsNotFound() throws Exception {
        BalanceDto dto = new BalanceDto(AMOUNT);
        doThrow(new NotFoundException(USER_NOT_FOUND)).when(balanceService).payment(dto, USER_ID);

        mockMvc.perform(post(BASE_URL + PAYMENT)
                .header(USER_ID_HEADER, USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(NOT_FOUND));
    }

    @Test
    void payment_MissingUserId_ReturnsBadRequest() throws Exception {
        BalanceDto dto = new BalanceDto(AMOUNT);

        mockMvc.perform(post(BASE_URL + PAYMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

}