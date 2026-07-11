package com.tanerkaynar.nexuserp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "kullanicilar")
public class Kullanici {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kullaniciid")
    private Integer kullaniciid;

    @Column(name = "kullaniciadi", nullable = false, unique = true)
    private String kullaniciadi;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "aktifmi", nullable = false)
    private Boolean aktifmi = true;

    @Column(name = "kayittarihi", insertable = false, updatable = false)
    private LocalDateTime kayittarihi;

    @Column(name = "parolahash", nullable = false)
    private String parolahash;

    @OneToOne
    @JoinColumn(name = "personelid", unique = true)
    private Personel personel;
}