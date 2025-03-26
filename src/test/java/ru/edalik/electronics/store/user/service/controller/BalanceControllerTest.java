package ru.edalik.electronics.store.user.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.edalik.electronics.store.user.service.model.dto.BalanceDto;
import ru.edalik.electronics.store.user.service.model.exception.InsufficientFunds;
import ru.edalik.electronics.store.user.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.user.service.service.interfaces.BalanceService;

import java.math.BigDecimal;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
@AutoConfigureMockMvc(addFilters = false)
class BalanceControllerTest {

    static final String BASE_URL = "/api/v1/users/balance";
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
        when(balanceService.getBalance()).thenReturn(AMOUNT);

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amount").value(AMOUNT));
    }

    @Test
    void getUserBalance_UserNotFound_ReturnsNotFound() throws Exception {
        when(balanceService.getBalance()).thenThrow(new NotFoundException(USER_NOT_FOUND));

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(NOT_FOUND));
    }

    @Test
    void deposit_ValidRequest_ReturnsOk() throws Exception {
        BalanceDto dto = new BalanceDto(AMOUNT);

        mockMvc.perform(post(BASE_URL + DEPOSIT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());

        verify(balanceService).deposit(dto);
    }

    @Test
    void deposit_UserNotFound_ReturnsNotFound() throws Exception {
        BalanceDto dto = new BalanceDto(AMOUNT);
        doThrow(new NotFoundException(USER_NOT_FOUND)).when(balanceService).deposit(dto);

        mockMvc.perform(post(BASE_URL + DEPOSIT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(NOT_FOUND));
    }

    @Test
    void payment_SufficientFunds_ReturnsOk() throws Exception {
        BalanceDto dto = new BalanceDto(AMOUNT);

        mockMvc.perform(post(BASE_URL + PAYMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());

        verify(balanceService).payment(dto);
    }

    @Test
    void payment_InsufficientFunds_ReturnsBadRequest() throws Exception {
        BalanceDto dto = new BalanceDto(AMOUNT);
        doThrow(new InsufficientFunds())
            .when(balanceService).payment(dto);

        mockMvc.perform(post(BASE_URL + PAYMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    @Test
    void payment_UserNotFound_ReturnsNotFound() throws Exception {
        BalanceDto dto = new BalanceDto(AMOUNT);
        doThrow(new NotFoundException(USER_NOT_FOUND)).when(balanceService).payment(dto);

        mockMvc.perform(post(BASE_URL + PAYMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(NOT_FOUND));
    }

}