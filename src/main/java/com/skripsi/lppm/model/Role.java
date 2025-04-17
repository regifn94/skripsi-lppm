package com.skripsi.lppm.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;
}
