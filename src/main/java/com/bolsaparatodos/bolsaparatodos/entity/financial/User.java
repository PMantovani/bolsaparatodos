package com.bolsaparatodos.bolsaparatodos.entity.financial;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String name;
    private String passwordHash;
    private boolean isAdmin;
    @Column(unique = true)
    private String apiKey;
}
