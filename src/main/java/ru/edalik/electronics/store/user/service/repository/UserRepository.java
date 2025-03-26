package ru.edalik.electronics.store.user.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.edalik.electronics.store.user.service.model.entity.User;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Modifying
    @Query("DELETE User u WHERE u.id = :id")
    int customDeleteById(UUID id);

    @Modifying
    @Query("UPDATE User u SET u.balance = u.balance + :amount WHERE u.id = :id")
    int deposit(BigDecimal amount, UUID id);

    @Modifying
    @Query("UPDATE User u SET u.balance = u.balance - :amount WHERE u.id = :id")
    int payment(BigDecimal amount, UUID id);

}