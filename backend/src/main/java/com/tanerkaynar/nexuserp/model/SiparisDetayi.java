package com.tanerkaynar.nexuserp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "siparisdetaylari")
public class SiparisDetayi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "siparisdetayid")
    private Integer siparisdetayid;

    @ManyToOne
    @JoinColumn(name = "siparisid", nullable = false)
    private Siparis siparis;

    @ManyToOne
    @JoinColumn(name = "urunid", nullable = false)
    private Urun urun;

    @Column(name = "miktar", nullable = false)
    private Integer miktar = 0;

    @Column(name = "birimfiyat", nullable = false)
    private BigDecimal birimfiyat = BigDecimal.ZERO;
}