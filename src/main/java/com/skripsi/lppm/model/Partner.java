package com.skripsi.lppm.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * as a mitra
 * */

@Data
@Entity
@Table
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String pic;
}
