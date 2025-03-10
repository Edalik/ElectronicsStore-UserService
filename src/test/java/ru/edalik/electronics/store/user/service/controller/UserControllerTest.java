package ru.edalik.electronics.store.user.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.edalik.electronics.store.user.service.mapper.UserMapper;
import ru.edalik.electronics.store.user.service.model.dto.UserDto;
import ru.edalik.electronics.store.user.service.model.dto.UserRegisterDto;
import ru.edalik.electronics.store.user.service.model.dto.UserUpdateDto;
import ru.edalik.electronics.store.user.service.model.entity.User;
import ru.edalik.electronics.store.user.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.user.service.model.exception.UserAlreadyExistsException;
import ru.edalik.electronics.store.user.service.service.interfaces.UserService;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    static final String BASE_PATH = "/api/v1/users";
    static final UUID ID = UUID.randomUUID();
    static final String LOGIN = "login";
    static final String PASSWORD = "password";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @MockitoBean
    UserMapper userMapper;

    @Test
    @SneakyThrows
    void register_ValidInput_ReturnsCreated() {
        UserRegisterDto registerDto = new UserRegisterDto(LOGIN, PASSWORD);
        User user = mock(User.class);
        UserDto expectedDto = mock(UserDto.class);

        when(userService.register(registerDto)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        MvcResult result = mockMvc.perform(
                post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerDto))
            )
            .andExpect(status().isCreated())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(expectedDto), result.getResponse().getContentAsString());
    }

    @Test
    @SneakyThrows
    void register_InvalidInput_ThrowsConflictException() {
        UserRegisterDto registerDto = new UserRegisterDto(LOGIN, PASSWORD);

        when(userService.register(registerDto)).thenThrow(UserAlreadyExistsException.class);

        mockMvc.perform(
                post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerDto))
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    @SneakyThrows
    void getUserById_ExistingUser_ReturnsUserDto() {
        User user = User.builder().id(ID).build();
        UserDto expectedDto = mock(UserDto.class);

        when(userService.findById(ID)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        MvcResult result = mockMvc.perform(get(BASE_PATH + "/{id}", ID))
            .andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(expectedDto), result.getResponse().getContentAsString());
    }

    @Test
    @SneakyThrows
    void getUserById_NotExistingUser_ThrowsNotFoundException() {
        when(userService.findById(ID)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(BASE_PATH + "/{id}", ID))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @SneakyThrows
    void update_ValidInput_ReturnsUpdatedUser() {
        UserUpdateDto updateDto = new UserUpdateDto(
            ID,
            "Иван",
            "Иванов",
            "Иванович",
            true,
            LocalDate.of(1990, 1, 1),
            "+79991234567",
            "new@example.com"
        );
        User updatedUser = mock(User.class);
        UserDto expectedDto = mock(UserDto.class);

        when(userService.update(updateDto)).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(expectedDto);

        MvcResult result = mockMvc.perform(
                put(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto))
            )
            .andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(expectedDto), result.getResponse().getContentAsString());
    }

    @Test
    @SneakyThrows
    void update_InvalidPhoneNumber_ReturnsValidationError() {
        UserUpdateDto invalidDto = new UserUpdateDto(
            ID,
            "John",
            "Doe",
            null,
            true,
            LocalDate.now(),
            "invalid-phone",
            "invalid-email"
        );

        mockMvc.perform(
                put(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Error"))
            .andExpect(jsonPath("$.fields[*].field")
                .value(containsInAnyOrder("phoneNumber", "email")));
    }

    @Test
    @SneakyThrows
    void register_InvalidInput_ReturnsValidationErrors() {
        UserRegisterDto invalidDto = new UserRegisterDto(
            "qwe",
            "short"
        );

        mockMvc.perform(
                post(BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields[*].field")
                .value(containsInAnyOrder(
                    LOGIN,
                    PASSWORD)));
    }

    @Test
    @SneakyThrows
    void deleteUserById_ValidInput_ReturnsNoContent() {
        mockMvc.perform(delete(BASE_PATH + "/{id}", ID))
            .andExpect(status().isNoContent());
    }

}