package com.tanerkaynar.nexuserp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "makineler")
public class Makine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "makineid")
    private Integer makineid;

    @Column(name = "makineadi", nullable = false)
    private String makineadi;

    @Column(name = "makinekodu", nullable = false)
    private String makinekodu;

    @Column(name = "durum", nullable = false)
    private String durum = "Aktif";

    @Column(name = "kayittarihi", insertable = false, updatable = false)
    private LocalDateTime kayittarihi;
}