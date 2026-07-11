package com.tanerkaynar.nexuserp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "uretimkayitlari")
public class UretimKaydi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uretimid")
    private Integer uretimid;

    @ManyToOne
    @JoinColumn(name = "urunid", nullable = false)
    private Urun urun;

    @ManyToOne
    @JoinColumn(name = "makineid", nullable = false)
    private Makine makine;

    @ManyToOne
    @JoinColumn(name = "personelid", nullable = false)
    private Personel personel;

    @Column(name = "uretimadedi", nullable = false)
    private Integer uretimadedi = 0;

    @Column(name = "uretimtarihi", insertable = false, updatable = false)
    private LocalDateTime uretimtarihi;

    @Column(name = "aciklama", columnDefinition = "TEXT")
    private String aciklama;
}