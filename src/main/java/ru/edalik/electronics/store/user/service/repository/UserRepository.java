package ru.edalik.electronics.store.user.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.edalik.electronics.store.user.service.model.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);

    @Modifying
    @Query("DELETE User u WHERE u.id = :id")
    int customDeleteById(UUID id);

}