package ru.edalik.electronics.store.user.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edalik.electronics.store.user.service.mapper.UserMapper;
import ru.edalik.electronics.store.user.service.model.dto.UserRegisterDto;
import ru.edalik.electronics.store.user.service.model.dto.UserUpdateDto;
import ru.edalik.electronics.store.user.service.model.entity.User;
import ru.edalik.electronics.store.user.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.user.service.model.exception.UserAlreadyExistsException;
import ru.edalik.electronics.store.user.service.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    static final UUID USER_ID = UUID.randomUUID();
    static final String LOGIN = "login";

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserServiceImpl userService;

    final User testUser = mock(User.class);

    final UserRegisterDto registerDto = mock(UserRegisterDto.class);

    final UserUpdateDto updateDto = mock(UserUpdateDto.class);

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

        User result = userService.findById(USER_ID);

        assertEquals(testUser, result);
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenUserNotExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(USER_ID));
    }

    @Test
    void findByLogin_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByLogin(LOGIN)).thenReturn(Optional.of(testUser));

        User result = userService.findByLogin(LOGIN);

        assertEquals(testUser, result);
    }

    @Test
    void findByLogin_ShouldThrowNotFoundException_WhenUserNotExists() {
        when(userRepository.findByLogin(LOGIN)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByLogin(LOGIN));
    }

    @Test
    void existsByLogin_ShouldReturnTrue_WhenLoginExists() {
        when(userRepository.existsByLogin(LOGIN)).thenReturn(true);

        assertTrue(userService.existsByLogin(LOGIN));
    }

    @Test
    void existsByLogin_ShouldReturnFalse_WhenLoginNotExists() {
        when(userRepository.existsByLogin(LOGIN)).thenReturn(false);

        assertFalse(userService.existsByLogin(LOGIN));
    }

    @Test
    void register_ShouldSaveNewUser_WhenLoginIsUnique() {
        when(registerDto.login()).thenReturn(LOGIN);
        when(userRepository.existsByLogin(LOGIN)).thenReturn(false);
        when(userMapper.toEntity(registerDto)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.register(registerDto);

        assertEquals(testUser, result);
    }

    @Test
    void register_ShouldThrowException_WhenLoginExists() {
        when(registerDto.login()).thenReturn(LOGIN);
        when(userRepository.existsByLogin(LOGIN)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(registerDto));
    }

    @Test
    void update_ShouldUpdateUser_WhenUserExists() {
        User updatedUser = mock(User.class);
        when(updateDto.id()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(userMapper.partialUpdate(updateDto, testUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.update(updateDto);

        assertEquals(updatedUser, result);
    }

    @Test
    void update_ShouldThrowException_WhenUserNotExists() {
        when(updateDto.id()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(updateDto));
    }

    @Test
    void delete_ShouldCallCustomDelete_WhenUserExists() {
        when(userRepository.customDeleteById(USER_ID)).thenReturn(1);

        assertDoesNotThrow(() -> userService.delete(USER_ID));
        verify(userRepository).customDeleteById(USER_ID);
    }

    @Test
    void delete_ShouldThrowException_WhenUserNotExists() {
        when(userRepository.customDeleteById(USER_ID)).thenReturn(0);

        assertThrows(NotFoundException.class, () -> userService.delete(USER_ID));
        verify(userRepository).customDeleteById(USER_ID);
    }

}