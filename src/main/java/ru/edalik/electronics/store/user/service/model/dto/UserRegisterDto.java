package ru.edalik.electronics.store.user.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

@Schema(description = "DTO для регистрации пользователя")
public record UserRegisterDto(
    @Schema(
        description = "Логин пользователя (4-50 символов)",
        example = "login",
        minLength = 4,
        maxLength = 50
    )
    @Length(min = 4, max = 50)
    String login,

    @Schema(
        description = "Пароль (8-50 символов)",
        example = "securePassword123!",
        minLength = 8,
        maxLength = 50
    )
    @Length(min = 8, max = 50)
    String password
) {

}