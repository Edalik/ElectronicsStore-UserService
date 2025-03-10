package ru.edalik.electronics.store.user.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.edalik.electronics.store.user.service.mapper.UserMapper;
import ru.edalik.electronics.store.user.service.model.dto.UserDto;
import ru.edalik.electronics.store.user.service.model.dto.UserRegisterDto;
import ru.edalik.electronics.store.user.service.model.dto.UserUpdateDto;
import ru.edalik.electronics.store.user.service.model.dto.exception.ErrorDto;
import ru.edalik.electronics.store.user.service.model.dto.exception.ValidationErrorDto;
import ru.edalik.electronics.store.user.service.service.interfaces.UserService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User Service", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @Operation(
        summary = "Регистрация нового пользователя",
        description = "Создает нового пользователя в системе"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Пользователь успешно создан",
        content = @Content(schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Невалидные входные данные",
        content = @Content(schema = @Schema(implementation = ValidationErrorDto.class))
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody @Valid UserRegisterDto dto) {
        return userMapper.toDto(userService.register(dto));
    }

    @Operation(
        summary = "Обновление данных пользователя",
        description = "Обновляет информацию о существующем пользователе"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Данные пользователя обновлены",
        content = @Content(schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Невалидные входные данные",
        content = @Content(schema = @Schema(implementation = ValidationErrorDto.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Пользователь не найден",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @PutMapping
    public UserDto update(@RequestBody @Valid UserUpdateDto dto) {
        return userMapper.toDto(userService.update(dto));
    }

    @Operation(
        summary = "Получение пользователя по ID",
        description = "Возвращает полную информацию о пользователе"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Пользователь найден",
        content = @Content(schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Пользователь не найден",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @GetMapping("/{id}")
    public UserDto getUserById(
        @Parameter(
            description = "UUID пользователя",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @PathVariable UUID id
    ) {
        return userMapper.toDto(userService.findById(id));
    }

    @Operation(
        summary = "Удаление пользователя",
        description = "Удаляет пользователя из системы"
    )
    @ApiResponse(
        responseCode = "204",
        description = "Пользователь успешно удален"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Пользователь не найден",
        content = @Content(schema = @Schema(implementation = ErrorDto.class))
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(
        @Parameter(
            description = "UUID пользователя",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        @PathVariable UUID id
    ) {
        userService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}