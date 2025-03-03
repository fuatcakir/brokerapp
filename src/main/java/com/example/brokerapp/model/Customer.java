package com.example.brokerapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "Customers")
public class Customer {
    @Id
    private Long id;
    private String username;
    private String password;
}
