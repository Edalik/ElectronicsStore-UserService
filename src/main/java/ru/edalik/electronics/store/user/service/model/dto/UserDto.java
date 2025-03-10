package ru.edalik.electronics.store.user.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Schema(description = "DTO для представления пользователя")
public record UserDto(
    @Schema(
        description = "UUID пользователя",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    UUID id,

    @Schema(
        description = "Логин пользователя",
        example = "login"
    )
    String login,

    @Schema(
        description = "Имя пользователя",
        example = "Иван"
    )
    String name,

    @Schema(
        description = "Фамилия пользователя",
        example = "Иванов"
    )
    String surname,

    @Schema(
        description = "Отчество пользователя",
        example = "Иванович"
    )
    String patronymic,

    @Schema(
        description = "Пол пользователя",
        example = "true (мужской)"
    )
    Boolean gender,

    @Schema(
        description = "Дата рождения",
        example = "1990-01-01"
    )
    LocalDate birthdate,

    @Schema(
        description = "Номер телефона",
        example = "+79991234567"
    )
    String phoneNumber,

    @Schema(
        description = "Email адрес",
        example = "user@example.com"
    )
    String email,

    @Schema(
        description = "Баланс пользователя",
        example = "1500.50"
    )
    BigDecimal balance,

    @Schema(
        description = "Дата создания пользователя",
        example = "2024-02-20T14:30:45.123+03:00"
    )
    ZonedDateTime createdTime,

    @Schema(
        description = "Дата последнего обновления",
        example = "2024-02-20T14:30:45.123+03:00"
    )
    ZonedDateTime updatedTime
) {

}