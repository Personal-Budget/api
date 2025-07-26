package com.biancodavide3.budgeting.db.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String email;
    private String passwordHash;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    private List<CategoryEntity> categories;
}