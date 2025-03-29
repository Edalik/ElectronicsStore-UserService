package ru.edalik.electronics.store.user.service.service;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Service;
import ru.edalik.electronics.store.user.service.model.dto.BalanceDto;
import ru.edalik.electronics.store.user.service.model.entity.User;
import ru.edalik.electronics.store.user.service.model.exception.InsufficientFunds;
import ru.edalik.electronics.store.user.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.user.service.repository.UserRepository;
import ru.edalik.electronics.store.user.service.service.interfaces.BalanceService;
import ru.edalik.electronics.store.user.service.service.security.UserContextService;

import java.math.BigDecimal;
import java.util.UUID;

import static ru.edalik.electronics.store.user.service.service.UserServiceImpl.USER_NOT_FOUND_BY_ID;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final UserRepository userRepository;

    private final UserContextService userContextService;

    public BigDecimal getBalance() {
        UUID id = userContextService.getUserGuid();
        User user = userRepository.findById(id)
            .orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_BY_ID.formatted(id))
            );

        return user.getBalance();
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    public void deposit(BalanceDto dto) {
        UUID id = userContextService.getUserGuid();
        int rowsAffected = userRepository.deposit(dto.amount(), id);
        if (rowsAffected < 1) {
            throw new NotFoundException(USER_NOT_FOUND_BY_ID.formatted(id));
        }
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    public void payment(BalanceDto dto) {
        UUID id = userContextService.getUserGuid();
        User user = userRepository.findById(id)
            .orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_BY_ID.formatted(id))
            );

        if (user.getBalance().compareTo(dto.amount()) < 0) {
            throw new InsufficientFunds();
        }

        int rowsAffected = userRepository.payment(dto.amount(), id);
        if (rowsAffected < 1) {
            throw new NotFoundException(USER_NOT_FOUND_BY_ID.formatted(id));
        }
    }

}
