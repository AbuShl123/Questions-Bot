package com.abu.PracticeBot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    private Long chatId;

    private String firstname;

    private String lastname;

    private String username;

    private Timestamp registerDate;
}

