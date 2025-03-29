package ru.edalik.electronics.store.user.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edalik.electronics.store.user.service.model.dto.BalanceDto;
import ru.edalik.electronics.store.user.service.model.entity.User;
import ru.edalik.electronics.store.user.service.model.exception.InsufficientFunds;
import ru.edalik.electronics.store.user.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.user.service.repository.UserRepository;
import ru.edalik.electronics.store.user.service.service.security.UserContextService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.edalik.electronics.store.user.service.service.UserServiceImpl.USER_NOT_FOUND_BY_ID;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    static final UUID USER_ID = UUID.randomUUID();
    static final BigDecimal BALANCE = BigDecimal.valueOf(100.0);
    static final BigDecimal AMOUNT = BigDecimal.valueOf(50.0);

    @Mock
    UserRepository userRepository;

    @Mock
    UserContextService userContextService;

    @InjectMocks
    BalanceServiceImpl balanceService;

    User testUser = User.builder()
        .id(USER_ID)
        .balance(BALANCE)
        .build();

    BalanceDto balanceDto = new BalanceDto(AMOUNT);

    @BeforeEach
    void setUp() {
        lenient().when(userContextService.getUserGuid()).thenReturn(USER_ID);
    }

    @Test
    void getBalance_ShouldReturnBalance_WhenUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

        BigDecimal result = balanceService.getBalance();

        assertEquals(BALANCE, result);
    }

    @Test
    void getBalance_ShouldThrowNotFoundException_WhenUserNotExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> balanceService.getBalance());
    }

    @Test
    void deposit_ShouldUpdateBalance_WhenUserExists() {
        when(userRepository.deposit(balanceDto.amount(), USER_ID)).thenReturn(1);
        balanceService.deposit(balanceDto);

        verify(userRepository).deposit(balanceDto.amount(), USER_ID);
    }

    @Test
    void deposit_ShouldThrowNotFoundException_WhenUserNotExists() {
        when(userRepository.deposit(balanceDto.amount(), USER_ID)).thenReturn(0);

        Exception exception = assertThrows(
            NotFoundException.class,
            () -> balanceService.deposit(balanceDto)
        );

        assertEquals(USER_NOT_FOUND_BY_ID.formatted(USER_ID), exception.getMessage());
    }

    @Test
    void payment_ShouldProcessPayment_WhenSufficientFunds() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.payment(balanceDto.amount(), USER_ID)).thenReturn(1);

        balanceService.payment(balanceDto);

        verify(userRepository).payment(balanceDto.amount(), USER_ID);
    }

    @Test
    void payment_ShouldThrowInsufficientFunds_WhenBalanceTooLow() {
        User userWithLowBalance = User.builder()
            .id(USER_ID)
            .balance(BigDecimal.TEN)
            .build();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userWithLowBalance));

        assertThrows(
            InsufficientFunds.class,
            () -> balanceService.payment(balanceDto)
        );
    }

    @Test
    void payment_ShouldThrowNotFoundException_WhenUserNotExistsInFind() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> balanceService.payment(balanceDto)
        );
    }

    @Test
    void payment_ShouldThrowNotFoundException_WhenUserNotExistsInUpdate() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.payment(balanceDto.amount(), USER_ID)).thenReturn(0);

        Exception exception = assertThrows(
            NotFoundException.class,
            () -> balanceService.payment(balanceDto)
        );

        assertEquals(USER_NOT_FOUND_BY_ID.formatted(USER_ID), exception.getMessage());
    }

}