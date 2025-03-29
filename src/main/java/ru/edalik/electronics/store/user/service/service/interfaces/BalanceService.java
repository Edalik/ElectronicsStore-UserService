package ru.edalik.electronics.store.user.service.service.interfaces;

import ru.edalik.electronics.store.user.service.model.dto.BalanceDto;

import java.math.BigDecimal;

public interface BalanceService {

    BigDecimal getBalance();

    void deposit(BalanceDto dto);

    void payment(BalanceDto dto);

}