package com.tanerkaynar.nexuserp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "urunler")
public class Urun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "urunid")
    private Integer urunid;

    @Column(name = "urunadi", nullable = false)
    private String urunadi;

    @Column(name = "stokmiktari", nullable = false)
    private Integer stokmiktari = 0;

    @Column(name = "birimfiyat", nullable = false)
    private BigDecimal birimfiyat = BigDecimal.ZERO;

    @Column(name = "aktifmi", nullable = false)
    private Boolean aktifmi = true;

    @Column(name = "kayittarihi", insertable = false, updatable = false)
    private LocalDateTime kayittarihi;
}