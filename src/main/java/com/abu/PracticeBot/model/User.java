package com.abu.PracticeBot.model;

import jakarta.persistence.*;
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

