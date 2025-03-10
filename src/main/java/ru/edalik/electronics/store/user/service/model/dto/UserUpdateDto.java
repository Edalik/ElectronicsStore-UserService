package ru.edalik.electronics.store.user.service.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "DTO для обновления данных пользователя")
public record UserUpdateDto(
    @Schema(
        description = "UUID пользователя для обновления",
        example = "550e8400-e29b-41d4-a716-446655440000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    UUID id,

    @Schema(
        description = "Имя пользователя (макс. 50 символов)",
        example = "Иван",
        maxLength = 50
    )
    @Length(max = 50)
    String name,

    @Schema(
        description = "Фамилия пользователя (макс. 50 символов)",
        example = "Иванов",
        maxLength = 50
    )
    @Length(max = 50)
    String surname,

    @Schema(
        description = "Отчество пользователя (макс. 50 символов)",
        example = "Иванович",
        maxLength = 50
    )
    @Length(max = 50)
    String patronymic,

    @Schema(
        description = "Пол пользователя (true - мужской, false - женский)",
        example = "true"
    )
    Boolean gender,

    @Schema(
        description = "Дата рождения в формате yyyy.MM.dd",
        example = "1990.01.01"
    )
    @JsonFormat(pattern = "yyyy.MM.dd")
    LocalDate birthdate,

    @Schema(
        description = "Номер телефона в форматах: +7XXXXXXXXXX, 7XXXXXXXXXX, 8XXXXXXXXXX",
        example = "+79991234567"
    )
    @Pattern(regexp = "^((\\+7|7|8)+(\\d){10})$")
    String phoneNumber,

    @Schema(
        description = "Email адрес",
        example = "user@example.com"
    )
    @Email
    String email
) {

}