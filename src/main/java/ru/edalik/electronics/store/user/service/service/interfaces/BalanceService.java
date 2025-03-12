package ru.edalik.electronics.store.user.service.service.interfaces;

import ru.edalik.electronics.store.user.service.model.dto.BalanceDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface BalanceService {

    BigDecimal getBalance(UUID id);

    void deposit(BalanceDto dto, UUID id);

    void payment(BalanceDto dto, UUID id);

}