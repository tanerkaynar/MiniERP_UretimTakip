package com.tanerkaynar.nexuserp.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "musteriler")
public class Musteri {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "musteriid")
    private Integer musteriid;

    @Column(name = "musteriadi", nullable = false)
    private String musteriadi;
}