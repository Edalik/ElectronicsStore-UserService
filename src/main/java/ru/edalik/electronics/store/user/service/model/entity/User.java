package ru.edalik.electronics.store.user.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "login", updatable = false)
    private String login;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "gender")
    private Boolean gender;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email", updatable = false)
    private String email;

    @Column(name = "balance", updatable = false)
    private BigDecimal balance;

    @CreationTimestamp
    @Column(name = "created_time", updatable = false)
    private ZonedDateTime createdTime;

    @UpdateTimestamp
    @Column(name = "updated_time")
    private ZonedDateTime updatedTime;

}