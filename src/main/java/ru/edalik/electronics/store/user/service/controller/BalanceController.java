package ru.edalik.electronics.store.user.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.edalik.electronics.store.user.service.model.dto.BalanceDto;
import ru.edalik.electronics.store.user.service.model.dto.exception.ErrorDto;
import ru.edalik.electronics.store.user.service.service.interfaces.BalanceService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/balance")
@Tag(name = "Balance Controller", description = "API для взаимодействия с балансом пользователя")
public class BalanceController {

    private final BalanceService balanceService;

    @Operation(
        summary = "Получение баланса пользователя",
        description = "Возвращает баланс пользователя"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Пользователь найден",
        content = @Content(schema = @Schema(implementation = BalanceDto.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Пользователь не найден",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @GetMapping
    public ResponseEntity<BalanceDto> getUserBalance(
        @Parameter(
            description = "UUID пользователя",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @RequestHeader("User-Id") UUID userId
    ) {
        return new ResponseEntity<>(new BalanceDto(balanceService.getBalance(userId)), HttpStatus.OK);
    }

    @Operation(
        summary = "Пополнение счета",
        description = "Пополняет счет пользователя"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Счет успешно пополнен"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Пользователь не найден",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(
        @RequestBody BalanceDto dto,
        @RequestHeader("User-Id") UUID userId
    ) {
        balanceService.deposit(dto, userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
        summary = "Оплата покупки",
        description = "Оплачивает покупку пользователя"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Покупка успешно оплачена"
    )
    @ApiResponse(
        responseCode = "400",
        description = "Недостаточно средств",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Пользователь не найден",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @PostMapping("/payment")
    public ResponseEntity<Void> payment(
        @RequestBody BalanceDto dto,
        @RequestHeader("User-Id") UUID userId
    ) {
        balanceService.payment(dto, userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}