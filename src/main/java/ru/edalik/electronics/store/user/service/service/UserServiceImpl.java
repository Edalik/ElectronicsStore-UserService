package ru.edalik.electronics.store.user.service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.edalik.electronics.store.user.service.mapper.UserMapper;
import ru.edalik.electronics.store.user.service.model.dto.UserRegisterDto;
import ru.edalik.electronics.store.user.service.model.dto.UserUpdateDto;
import ru.edalik.electronics.store.user.service.model.entity.User;
import ru.edalik.electronics.store.user.service.model.exception.NotFoundException;
import ru.edalik.electronics.store.user.service.model.exception.UserAlreadyExistsException;
import ru.edalik.electronics.store.user.service.repository.UserRepository;
import ru.edalik.electronics.store.user.service.service.interfaces.UserService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String USER_NOT_FOUND_BY_ID = "User with id: %s was not found";
    private static final String USER_NOT_FOUND_BY_LOGIN = "User with login: %s was not found";
    private static final String USER_ALREADY_EXISTS = "User with login: %s already exists";

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public User findById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_BY_ID.formatted(id)));
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_BY_LOGIN.formatted(login)));
    }

    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    public User register(UserRegisterDto dto) {
        if (existsByLogin(dto.login())) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS.formatted(dto.login()));
        }

        return userRepository.save(userMapper.toEntity(dto));
    }

    public User update(UserUpdateDto dto) {
        Optional<User> user = userRepository.findById(dto.id());
        if (user.isEmpty()) {
            throw new NotFoundException(USER_NOT_FOUND_BY_ID.formatted(dto.id()));
        }

        return userRepository.save(userMapper.partialUpdate(dto, user.get()));
    }

    @Transactional
    public void delete(UUID id) {
        int rowsAffected = userRepository.customDeleteById(id);
        if (rowsAffected < 1) {
            throw new NotFoundException(USER_NOT_FOUND_BY_ID.formatted(id));
        }
    }

}