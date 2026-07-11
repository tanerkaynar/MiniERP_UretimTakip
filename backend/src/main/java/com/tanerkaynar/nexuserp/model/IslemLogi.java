package com.tanerkaynar.nexuserp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "islemloglari")
public class IslemLogi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logid")
    private Integer logid;

    @Column(name = "kullaniciadi", nullable = false)
    private String kullaniciadi;

    @Column(name = "islemturu", nullable = false)
    private String islemturu;

    @Column(name = "aciklama")
    private String aciklama;

    @Column(name = "islemtarihi", insertable = false, updatable = false)
    private LocalDateTime islemtarihi;
}