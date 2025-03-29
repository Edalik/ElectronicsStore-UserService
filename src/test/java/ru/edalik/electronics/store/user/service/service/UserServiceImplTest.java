package ru.edalik.electronics.store.user.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edalik.electronics.store.user.service.mapper.UserMapper;
import ru.edalik.electronics.store.user.service.model.dto.UserUpdateDto;
import ru.edalik.electronics.store.user.service.model.entity.User;
import ru.edalik.electronics.store.user.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.user.service.repository.UserRepository;
import ru.edalik.electronics.store.user.service.service.security.UserContextService;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    static final UUID USER_ID = UUID.randomUUID();
    static final String LOGIN = "login";
    static final String EMAIL = "email";

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    UserContextService userContextService;

    @Spy
    @InjectMocks
    UserServiceImpl userService;

    final User testUser = User.builder().id(USER_ID).login(LOGIN).email(EMAIL).build();

    final UserUpdateDto updateDto = mock(UserUpdateDto.class);

    @BeforeEach
    void setUp() {
        lenient().when(userContextService.getUserGuid()).thenReturn(USER_ID);
        lenient().when(userContextService.getPreferredUserName()).thenReturn(LOGIN);
        lenient().when(userContextService.getEmail()).thenReturn(EMAIL);
    }

    @Test
    void getOrCreate_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

        User result = userService.getOrCreate();

        assertEquals(testUser, result);
    }

    @Test
    void getOrCreate_ShouldRegister_WhenUserNotExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        doReturn(testUser).when(userService).register();

        User result = userService.getOrCreate();

        assertEquals(testUser, result);
    }

    @Test
    void register_ShouldSaveNewUser() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(captor.capture())).thenReturn(testUser);

        User result = userService.register();

        User capturedUser = captor.getValue();
        assertEquals(testUser, result);
        assertThat(capturedUser).usingRecursiveComparison().isEqualTo(testUser);
    }

    @Test
    void update_ShouldUpdateUser_WhenUserExists() {
        User updatedUser = mock(User.class);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(userMapper.partialUpdate(updateDto, testUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.update(updateDto);

        assertEquals(updatedUser, result);
    }

    @Test
    void update_ShouldThrowException_WhenUserNotExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(updateDto));
    }

    @Test
    void delete_ShouldCallCustomDelete_WhenUserExists() {
        when(userRepository.customDeleteById(USER_ID)).thenReturn(1);

        assertDoesNotThrow(() -> userService.delete());
        verify(userRepository).customDeleteById(USER_ID);
    }

    @Test
    void delete_ShouldThrowException_WhenUserNotExists() {
        when(userRepository.customDeleteById(USER_ID)).thenReturn(0);

        assertThrows(NotFoundException.class, () -> userService.delete());
        verify(userRepository).customDeleteById(USER_ID);
    }

}