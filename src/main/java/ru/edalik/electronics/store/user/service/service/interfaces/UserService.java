package ru.edalik.electronics.store.user.service.service.interfaces;

import ru.edalik.electronics.store.user.service.model.dto.UserUpdateDto;
import ru.edalik.electronics.store.user.service.model.entity.User;

public interface UserService {

    User getOrCreate();

    User register();

    User update(UserUpdateDto dto);

    void delete();

}