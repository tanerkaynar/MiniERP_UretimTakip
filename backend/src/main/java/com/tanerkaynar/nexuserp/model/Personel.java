package com.tanerkaynar.nexuserp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "personeller")
public class Personel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personelid")
    private Integer personelid;

    @Column(name = "adsoyad", nullable = false)
    private String adsoyad;

    @Column(name = "departman")
    private String departman;

    @Column(name = "aktifmi", nullable = false)
    private Boolean aktifmi = true;

    @Column(name = "kayittarihi", insertable = false, updatable = false)
    private LocalDateTime kayittarihi;
}