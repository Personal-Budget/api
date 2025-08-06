package com.biancodavide3.budgeting.db.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    private Long id;
    private UUID supabaseId;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<CategoryEntity> categories;
}