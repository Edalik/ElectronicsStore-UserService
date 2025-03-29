package ru.edalik.electronics.store.user.service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.edalik.electronics.store.user.service.mapper.UserMapper;
import ru.edalik.electronics.store.user.service.model.dto.UserUpdateDto;
import ru.edalik.electronics.store.user.service.model.entity.User;
import ru.edalik.electronics.store.user.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.user.service.repository.UserRepository;
import ru.edalik.electronics.store.user.service.service.interfaces.UserService;
import ru.edalik.electronics.store.user.service.service.security.UserContextService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String USER_NOT_FOUND_BY_ID = "User with id: %s was not found";

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserContextService userContextService;

    public User getOrCreate() {
        return userRepository.findById(userContextService.getUserGuid())
            .orElseGet(this::register);
    }

    public User register() {
        User user = User.builder()
            .id(userContextService.getUserGuid())
            .login(userContextService.getPreferredUserName())
            .email(userContextService.getEmail())
            .build();

        return userRepository.save(user);
    }

    public User update(UserUpdateDto dto) {
        UUID id = userContextService.getUserGuid();
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException(USER_NOT_FOUND_BY_ID.formatted(id));
        }

        return userRepository.save(userMapper.partialUpdate(dto, user.get()));
    }

    @Transactional
    public void delete() {
        UUID id = userContextService.getUserGuid();
        int rowsAffected = userRepository.customDeleteById(id);
        if (rowsAffected < 1) {
            throw new NotFoundException(USER_NOT_FOUND_BY_ID.formatted(id));
        }
    }

}