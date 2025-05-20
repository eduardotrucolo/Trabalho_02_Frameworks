package com.example.SpringAula2.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String senha;
    private String role; // Ex: "ADMIN" ou "USER"

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}
