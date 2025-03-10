package ru.edalik.electronics.store.user.service.service.interfaces;

import ru.edalik.electronics.store.user.service.model.dto.UserRegisterDto;
import ru.edalik.electronics.store.user.service.model.dto.UserUpdateDto;
import ru.edalik.electronics.store.user.service.model.entity.User;

import java.util.UUID;

public interface UserService {

    User findById(UUID id);

    User findByLogin(String login);

    boolean existsByLogin(String login);

    User register(UserRegisterDto dto);

    User update(UserUpdateDto dto);

    void delete(UUID id);

}