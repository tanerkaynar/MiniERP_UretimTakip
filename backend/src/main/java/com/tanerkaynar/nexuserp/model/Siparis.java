package com.tanerkaynar.nexuserp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "siparisler")
public class Siparis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "siparisid")
    private Integer siparisid;

    @ManyToOne
    @JoinColumn(name = "musteriid", nullable = false)
    private Musteri musteri;

    @Column(name = "siparistarihi", insertable = false, updatable = false)
    private LocalDateTime siparistarihi;

    @Column(name = "durum", nullable = false)
    private String durum = "Hazirlaniyor";

    @OneToMany(mappedBy = "siparis", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<SiparisDetayi> detaylar;
}