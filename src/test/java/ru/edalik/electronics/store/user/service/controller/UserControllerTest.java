package ru.edalik.electronics.store.user.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.edalik.electronics.store.user.service.mapper.UserMapper;
import ru.edalik.electronics.store.user.service.model.dto.UserDto;
import ru.edalik.electronics.store.user.service.model.dto.UserUpdateDto;
import ru.edalik.electronics.store.user.service.model.entity.User;
import ru.edalik.electronics.store.user.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.user.service.service.interfaces.UserService;

import java.time.LocalDate;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    static final String BASE_URL = "/api/v1/users";
    static final String USER_NOT_FOUND_MSG = "User not found";
    static final String NOT_FOUND = "Not Found";

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
    void getUserById_ExistingUser_ReturnsUserDto() {
        User user = mock(User.class);
        UserDto expectedDto = mock(UserDto.class);

        when(userService.getOrCreate()).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));
    }

    @Test
    @SneakyThrows
    void getUserById_UserNotFound_ReturnsNotFound() {
        when(userService.getOrCreate()).thenThrow(new NotFoundException(USER_NOT_FOUND_MSG));

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(NOT_FOUND));
    }

    @Test
    @SneakyThrows
    void update_ValidRequest_ReturnsUpdatedUser() {
        UserUpdateDto requestDto = mock(UserUpdateDto.class);
        User user = mock(User.class);
        UserDto expectedDto = mock(UserDto.class);

        when(userService.update(requestDto)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        mockMvc.perform(
                put(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void update_InvalidRequest_ReturnsBadRequest() {
        UserUpdateDto invalidDto = new UserUpdateDto(
            "John",
            "Doe",
            null,
            true,
            LocalDate.now(),
            "invalid-phone"
        );

        mockMvc.perform(
                put(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void update_UserNotFound_ReturnsNotFound() {
        UserUpdateDto requestDto = new UserUpdateDto(
            "Иван",
            "Иванов",
            "Иванович",
            true,
            LocalDate.of(1990, 1, 1),
            "+79991234567"
        );
        when(userService.update(requestDto)).thenThrow(new NotFoundException(USER_NOT_FOUND_MSG));

        mockMvc.perform(
                put(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(NOT_FOUND));
    }

    @Test
    @SneakyThrows
    void deleteUserById_ExistingUser_ReturnsNoContent() {
        mockMvc.perform(delete(BASE_URL))
            .andExpect(status().isNoContent());

        verify(userService).delete();
    }

    @Test
    @SneakyThrows
    void deleteUserById_UserNotFound_ReturnsNotFound() {
        doThrow(new NotFoundException(USER_NOT_FOUND_MSG)).when(userService).delete();

        mockMvc.perform(delete(BASE_URL))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(NOT_FOUND));
    }

}