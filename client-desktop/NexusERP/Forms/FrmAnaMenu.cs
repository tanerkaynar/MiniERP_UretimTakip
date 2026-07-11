using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;
using NexusERP.Helpers;

namespace NexusERP
{
    public class FrmAnaMenu : Form
    {
        private const int CardWidth = 260;
        private const int CardHeight = 100;
        private const int CardOuterWidth = 284;

        private readonly string kullaniciAdi;
        private readonly List<Panel> bolumPanelleri = new List<Panel>();
        private readonly Label lblSaat;
        private readonly Label lblDurum;
        private readonly Timer saatTimer;
        private Panel kartPaneli;

        private readonly string kullaniciRol;

        public bool OturumKapatildi { get; private set; }

        public FrmAnaMenu()
            : this("Kullanici", "Operator")
        {
        }

        public FrmAnaMenu(string kullaniciAdi, string kullaniciRol)
        {
            this.kullaniciAdi = string.IsNullOrWhiteSpace(kullaniciAdi) ? "Kullanici" : kullaniciAdi;
            this.kullaniciRol = string.IsNullOrWhiteSpace(kullaniciRol) ? "Operator" : kullaniciRol;

            Text = "Mini ERP - Uretim Takip Sistemi";
            Width = 1180;
            Height = 720;
            MinimumSize = new Size(1020, 640);
            StartPosition = FormStartPosition.CenterScreen;
            BackColor = Color.FromArgb(244, 247, 251);
            Font = new Font("Segoe UI", 10);
            AutoScaleMode = AutoScaleMode.Font;

            var anaYerlesim = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                ColumnCount = 2,
                RowCount = 1,
                BackColor = Color.FromArgb(244, 247, 251)
            };
            anaYerlesim.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 248));
            anaYerlesim.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100));

            Controls.Add(anaYerlesim);
            anaYerlesim.Controls.Add(SolMenuOlustur(), 0, 0);
            anaYerlesim.Controls.Add(IcerikOlustur(), 1, 0);

            lblSaat = new Label();
            lblDurum = new Label();
            saatTimer = new Timer { Interval = 1000 };
            saatTimer.Tick += (sender, e) => SaatGuncelle();

            HeaderBilgileriniBagla(anaYerlesim);
            SaatGuncelle();
            saatTimer.Start();

            Load += (sender, e) => NexusERP.Helpers.ThemeHelper.ApplyTheme(this);
        }

        private Control SolMenuOlustur()
        {
            var solPanel = new Panel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.FromArgb(20, 31, 46),
                Padding = new Padding(22, 24, 22, 20)
            };

            var solYerlesim = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                RowCount = 3,
                ColumnCount = 1,
                BackColor = Color.Transparent
            };
            solYerlesim.RowStyles.Add(new RowStyle(SizeType.Absolute, 122));
            solYerlesim.RowStyles.Add(new RowStyle(SizeType.Percent, 100));
            solYerlesim.RowStyles.Add(new RowStyle(SizeType.Absolute, 166));
            solPanel.Controls.Add(solYerlesim);

            var markaPanel = new Panel { Dock = DockStyle.Fill, BackColor = Color.Transparent };
            markaPanel.Controls.Add(new Label
            {
                Text = "NexusERP",
                Left = 0,
                Top = 4,
                Width = 190,
                Height = 34,
                ForeColor = Color.White,
                Font = new Font("Segoe UI", 20, FontStyle.Bold)
            });
            markaPanel.Controls.Add(new Label
            {
                Text = "Uretim Takip Sistemi",
                Left = 2,
                Top = 42,
                Width = 190,
                Height = 22,
                ForeColor = Color.FromArgb(184, 195, 210),
                Font = new Font("Segoe UI", 9)
            });
            string displayOturum = !string.IsNullOrWhiteSpace(SessionHelper.PersonelAdSoyad)
                ? SessionHelper.PersonelAdSoyad + " (" + SessionHelper.KullaniciAdi + ")"
                : SessionHelper.KullaniciAdi;

            markaPanel.Controls.Add(new Label
            {
                Text = "Oturum: " + displayOturum,
                Left = 2,
                Top = 78,
                Width = 190,
                Height = 24,
                ForeColor = Color.FromArgb(226, 232, 240),
                Font = new Font("Segoe UI", 9, FontStyle.Bold)
            });

            var navPanel = new FlowLayoutPanel
            {
                Dock = DockStyle.Fill,
                FlowDirection = FlowDirection.TopDown,
                WrapContents = false,
                BackColor = Color.Transparent
            };

            if (kullaniciRol == "Admin")
            {
                navPanel.Controls.Add(SolMenuButonu("Ürün Yönetimi", typeof(UrunYonetimiForm)));
                navPanel.Controls.Add(SolMenuButonu("Sistem Tanımlamaları", typeof(TanimlamalarForm)));
                navPanel.Controls.Add(SolMenuButonu("Üretim Girişi", typeof(UretimGirisForm)));
                navPanel.Controls.Add(SolMenuButonu("Stok Takip & Kontrol", typeof(StokTakipForm)));
                navPanel.Controls.Add(SolMenuButonu("Üretim Raporları", typeof(UretimRaporForm)));
            }
            else if (kullaniciRol == "Planlamaci")
            {
                navPanel.Controls.Add(SolMenuButonu("Ürün Yönetimi", typeof(UrunYonetimiForm)));
                navPanel.Controls.Add(SolMenuButonu("Sistem Tanımlamaları", typeof(TanimlamalarForm)));
                navPanel.Controls.Add(SolMenuButonu("Sipariş Girişi", typeof(SiparisGirisForm)));
                navPanel.Controls.Add(SolMenuButonu("Stok Takip & Kontrol", typeof(StokTakipForm)));
                navPanel.Controls.Add(SolMenuButonu("Üretim Raporları", typeof(UretimRaporForm)));
            }
            else if (kullaniciRol == "Operator")
            {
                navPanel.Controls.Add(SolMenuButonu("Üretim Girişi", typeof(UretimGirisForm)));
            }
            else if (kullaniciRol == "Sevkiyatci")
            {
                navPanel.Controls.Add(SolMenuButonu("Stok Takip & Kontrol", typeof(StokTakipForm)));
                navPanel.Controls.Add(SolMenuButonu("Sevkiyat Yönetimi", typeof(SevkiyatForm)));
            }

            var altPanel = new FlowLayoutPanel
            {
                Dock = DockStyle.Fill,
                FlowDirection = FlowDirection.TopDown,
                WrapContents = false,
                BackColor = Color.Transparent
            };
            altPanel.Controls.Add(SolMenuButonu("Sistem Ayarları", typeof(AyarlarForm)));
            altPanel.Controls.Add(SolMenuButonu("Oturumu Kapat", null, OturumuKapat));
            altPanel.Controls.Add(SolMenuButonu("Programdan Çıkış", null, (sender, e) => Close()));

            solYerlesim.Controls.Add(markaPanel, 0, 0);
            solYerlesim.Controls.Add(navPanel, 0, 1);
            solYerlesim.Controls.Add(altPanel, 0, 2);

            return solPanel;
        }

        private Control DashboardPanelOlustur()
        {
            var panel = new FlowLayoutPanel
            {
                Dock = DockStyle.Top,
                Height = 135,
                Margin = new Padding(0, 0, 0, 20),
                BackColor = Color.Transparent,
                WrapContents = false,
                AutoScroll = false
            };

            try
            {
                string json = NexusERP.Helpers.ApiClient.Get($"/api/dashboard/stats?rol={kullaniciRol}");
                var stats = Newtonsoft.Json.JsonConvert.DeserializeObject<System.Collections.Generic.Dictionary<string, object>>(json);

                if (kullaniciRol == "Admin" || kullaniciRol == "Planlamaci")
                {
                    string urunSayisi = Convert.ToString(stats["urunSayisi"]);
                    string toplamUretim = Convert.ToString(stats["toplamUretim"]);
                    string aktifMakine = Convert.ToString(stats["aktifMakine"]);
                    string kritikStok = Convert.ToString(stats["kritikStok"]);

                    panel.Controls.Add(OzetKarti("Toplam Ürün", urunSayisi, Color.FromArgb(59, 130, 246)));
                    panel.Controls.Add(OzetKarti("Toplam Üretim", toplamUretim, Color.FromArgb(34, 197, 94)));
                    panel.Controls.Add(OzetKarti("Aktif Makineler", aktifMakine, Color.FromArgb(168, 85, 247)));
                    panel.Controls.Add(OzetKarti("Kritik Stok", kritikStok, Color.FromArgb(239, 68, 68)));
                }
                else if (kullaniciRol == "Operator")
                {
                    string bugunkuUretim = Convert.ToString(stats["bugunkuUretim"]);
                    string calisanMakine = Convert.ToString(stats["calisanMakine"]);
                    string sonUretimStr = Convert.ToString(stats["sonUretimStr"]);

                    panel.Controls.Add(OzetKarti("Bugün Toplam Üretim", bugunkuUretim, Color.FromArgb(34, 197, 94)));
                    panel.Controls.Add(OzetKarti("Aktif Makineler", calisanMakine, Color.FromArgb(59, 130, 246)));
                    panel.Controls.Add(OzetKarti("Son Kayıt Saati", sonUretimStr, Color.FromArgb(245, 158, 11)));
                }
                else if (kullaniciRol == "Sevkiyatci")
                {
                    string bekleyenSiparis = Convert.ToString(stats["bekleyenSiparis"]);
                    string kritikStok = Convert.ToString(stats["kritikStok"]);
                    string sevkEdilen = Convert.ToString(stats["sevkEdilen"]);

                    panel.Controls.Add(OzetKarti("Bekleyen Sevkiyat", bekleyenSiparis, Color.FromArgb(245, 158, 11)));
                    panel.Controls.Add(OzetKarti("Kritik Stok Ürün", kritikStok, Color.FromArgb(239, 68, 68)));
                    panel.Controls.Add(OzetKarti("Tamamlanan Sevkiyat", sevkEdilen, Color.FromArgb(34, 197, 94)));
                }
            }
            catch
            {
                panel.Controls.Add(OzetKarti("Veri Bağlantısı Yok", "-", Color.Gray));
            }

            return panel;
        }

        private Panel OzetKarti(string baslik, string deger, Color accentColor)
        {
            var card = new Panel
            {
                Width = 210,
                Height = 100,
                Margin = new Padding(0, 0, 16, 0),
                BackColor = Color.White,
                Padding = new Padding(16)
            };

            var accentBar = new Panel
            {
                Dock = DockStyle.Left,
                Width = 4,
                BackColor = accentColor
            };
            card.Controls.Add(accentBar);

            var titleLbl = new Label
            {
                Text = baslik.ToUpper(),
                Left = 16,
                Top = 16,
                Width = 180,
                Height = 18,
                ForeColor = Color.FromArgb(100, 116, 139),
                Font = new Font("Segoe UI", 8, FontStyle.Bold)
            };
            card.Controls.Add(titleLbl);

            var valueLbl = new Label
            {
                Text = deger,
                Left = 16,
                Top = 40,
                Width = 180,
                Height = 44,
                ForeColor = Color.FromArgb(30, 41, 59),
                Font = new Font("Segoe UI", 20, FontStyle.Bold)
            };
            card.Controls.Add(valueLbl);

            card.Paint += (sender, e) =>
            {
                ControlPaint.DrawBorder(e.Graphics, card.ClientRectangle, Color.FromArgb(226, 232, 240), ButtonBorderStyle.Solid);
            };

            return card;
        }

        private Control IcerikOlustur()
        {
            var icerikYerlesim = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                RowCount = 3,
                ColumnCount = 1,
                BackColor = Color.FromArgb(244, 247, 251)
            };
            icerikYerlesim.RowStyles.Add(new RowStyle(SizeType.Absolute, 128));
            icerikYerlesim.RowStyles.Add(new RowStyle(SizeType.Percent, 100));
            icerikYerlesim.RowStyles.Add(new RowStyle(SizeType.Absolute, 34));

            var headerPanel = HeaderOlustur();

            kartPaneli = new Panel
            {
                Dock = DockStyle.Fill,
                AutoScroll = true,
                BackColor = Color.FromArgb(244, 247, 251),
                Padding = new Padding(28, 4, 28, 20)
            };
            kartPaneli.SizeChanged += (sender, e) => BolumleriBoyutlandir();

            var kartYerlesim = new FlowLayoutPanel
            {
                Dock = DockStyle.Top,
                AutoSize = true,
                FlowDirection = FlowDirection.TopDown,
                WrapContents = false,
                BackColor = Color.Transparent
            };
            kartPaneli.Controls.Add(kartYerlesim);

            kartYerlesim.Controls.Add(DashboardPanelOlustur());

            if (kullaniciRol == "Admin")
            {
                BolumEkle(kartYerlesim, "Ürün ve Sistem Yönetimi", "Ürün kayıtları, müşteri, makine ve personel tanımlamaları.",
                    new MenuEntry("Ürün Yönetimi", "Ürün kaydet, güncelle, sil ve listele", typeof(UrunYonetimiForm), Color.FromArgb(14, 165, 233)),
                    new MenuEntry("Sistem Tanımlamaları", "Müşteri, Makine ve Personel kartları", typeof(TanimlamalarForm), Color.FromArgb(6, 182, 212)));

                BolumEkle(kartYerlesim, "Üretim Operasyonları", "Üretim kaydı ve ilişkili işlem ekranları.",
                    new MenuEntry("Üretim Girişi", "Personel, makine ve ürünle kayıt aç", typeof(UretimGirisForm), Color.FromArgb(34, 197, 94)));

                BolumEkle(kartYerlesim, "Stok, Sipariş ve Sevkiyat", "Stok kontrolü, sipariş girişi ve sevk işlemleri.",
                    new MenuEntry("Stok Takip & Kontrol", "Stok ve yeterlilik takibi", typeof(StokTakipForm), Color.FromArgb(245, 158, 11)),
                    new MenuEntry("Sipariş Girişi", "Müşteri siparişi ve detay kaydı", typeof(SiparisGirisForm), Color.FromArgb(251, 146, 60)),
                    new MenuEntry("Sevkiyat Yönetimi", "Siparişi sevk et ve stoktan düş", typeof(SevkiyatForm), Color.FromArgb(234, 88, 12)));

                BolumEkle(kartYerlesim, "Raporlar ve Analiz", "Üretim raporları, duruş analizi ve CSV aktarımı.",
                    new MenuEntry("Üretim Raporu", "Tarihli üretim raporu ve CSV aktarımı", typeof(UretimRaporForm), Color.FromArgb(139, 92, 246)),
                    new MenuEntry("Gruplu Rapor", "Üretimi gruplu şekilde incele", typeof(GrupluRaporForm), Color.FromArgb(168, 85, 247)),
                    new MenuEntry("Duruş Analizi", "Üretim yapmayan makineleri analiz et", typeof(DurusAnalizForm), Color.FromArgb(124, 58, 237)));

                BolumEkle(kartYerlesim, "Sistem Yardımcıları", "Bağlantı, log ve yardımcı veri ekranları.",
                    new MenuEntry("Sistem Günlükleri", "Son sistem işlemlerini gör", typeof(LogListeForm), Color.FromArgb(71, 85, 105)));
            }
            else if (kullaniciRol == "Planlamaci")
            {
                BolumEkle(kartYerlesim, "Ürün ve Sistem Yönetimi", "Ürün kayıtları, müşteri, makine ve personel tanımlamaları.",
                    new MenuEntry("Ürün Yönetimi", "Ürün kaydet, güncelle, sil ve listele", typeof(UrunYonetimiForm), Color.FromArgb(14, 165, 233)),
                    new MenuEntry("Sistem Tanımlamaları", "Müşteri, Makine ve Personel kartları", typeof(TanimlamalarForm), Color.FromArgb(6, 182, 212)));

                BolumEkle(kartYerlesim, "Stok ve Sipariş", "Stok kontrolü ve sipariş giriş işlemleri.",
                    new MenuEntry("Stok Takip & Kontrol", "Stok ve yeterlilik takibi", typeof(StokTakipForm), Color.FromArgb(245, 158, 11)),
                    new MenuEntry("Sipariş Girişi", "Müşteri siparişi ve detay kaydı", typeof(SiparisGirisForm), Color.FromArgb(251, 146, 60)));

                BolumEkle(kartYerlesim, "Raporlar ve Analiz", "Üretim raporları, duruş analizi ve CSV aktarımı.",
                    new MenuEntry("Üretim Raporu", "Tarihli üretim raporu ve CSV aktarımı", typeof(UretimRaporForm), Color.FromArgb(139, 92, 246)),
                    new MenuEntry("Gruplu Rapor", "Üretimi gruplu şekilde incele", typeof(GrupluRaporForm), Color.FromArgb(168, 85, 247)),
                    new MenuEntry("Duruş Analizi", "Üretim yapmayan makineleri analiz et", typeof(DurusAnalizForm), Color.FromArgb(124, 58, 237)));
            }
            else if (kullaniciRol == "Operator")
            {
                BolumEkle(kartYerlesim, "Üretim Operasyonları", "Üretim kaydı ve ilişkili işlem ekranları.",
                    new MenuEntry("Üretim Girişi", "Personel, makine ve ürünle kayıt aç", typeof(UretimGirisForm), Color.FromArgb(34, 197, 94)));
            }
            else if (kullaniciRol == "Sevkiyatci")
            {
                BolumEkle(kartYerlesim, "Stok, Sipariş ve Sevkiyat", "Stok kontrolü, sipariş girişi ve sevk işlemleri.",
                    new MenuEntry("Stok Takip & Kontrol", "Stok ve yeterlilik takibi", typeof(StokTakipForm), Color.FromArgb(245, 158, 11)),
                    new MenuEntry("Sevkiyat Yönetimi", "Siparişi sevk et ve stoktan düş", typeof(SevkiyatForm), Color.FromArgb(234, 88, 12)));
            }

            var durumPanel = new Panel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.FromArgb(235, 241, 248),
                Padding = new Padding(28, 7, 28, 0)
            };
            durumPanel.Controls.Add(new Label
            {
                Text = "Hazır",
                Dock = DockStyle.Fill,
                ForeColor = Color.FromArgb(71, 85, 105),
                Font = new Font("Segoe UI", 9)
            });

            icerikYerlesim.Controls.Add(headerPanel, 0, 0);
            icerikYerlesim.Controls.Add(kartPaneli, 0, 1);
            icerikYerlesim.Controls.Add(durumPanel, 0, 2);

            return icerikYerlesim;
        }

        private Panel HeaderOlustur()
        {
            var headerPanel = new Panel
            {
                Dock = DockStyle.Fill,
                BackColor = Color.White,
                Padding = new Padding(30, 18, 30, 16)
            };

            var headerYerlesim = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                ColumnCount = 2,
                RowCount = 1,
                BackColor = Color.White
            };
            headerYerlesim.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100));
            headerYerlesim.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 280));
            headerPanel.Controls.Add(headerYerlesim);

            var sol = new Panel { Dock = DockStyle.Fill, BackColor = Color.White };
            sol.Controls.Add(new Label
            {
                Text = "Ana Menu",
                Left = 0,
                Top = 4,
                Width = 420,
                Height = 42,
                ForeColor = Color.FromArgb(15, 23, 42),
                Font = new Font("Segoe UI", 24, FontStyle.Bold)
            });
            sol.Controls.Add(new Label
            {
                Text = "",
                Left = 3,
                Top = 51,
                Width = 480,
                Height = 24,
                ForeColor = Color.FromArgb(100, 116, 139),
                Font = new Font("Segoe UI", 10)
            });

            var sag = new Panel { Dock = DockStyle.Fill, BackColor = Color.White };
            sag.Controls.Add(new Label
            {
                Text = "AKTIF OTURUM",
                Left = 0,
                Top = 6,
                Width = 250,
                Height = 18,
                TextAlign = ContentAlignment.MiddleRight,
                ForeColor = Color.FromArgb(100, 116, 139),
                Font = new Font("Segoe UI", 8, FontStyle.Bold)
            });
            sag.Controls.Add(new Label
            {
                Text = !string.IsNullOrWhiteSpace(SessionHelper.PersonelAdSoyad)
                    ? SessionHelper.PersonelAdSoyad + " (" + SessionHelper.KullaniciAdi + ")"
                    : SessionHelper.KullaniciAdi,
                Left = 0,
                Top = 27,
                Width = 250,
                Height = 26,
                TextAlign = ContentAlignment.MiddleRight,
                ForeColor = Color.FromArgb(30, 41, 59),
                Font = new Font("Segoe UI", 12, FontStyle.Bold)
            });

            headerYerlesim.Controls.Add(sol, 0, 0);
            headerYerlesim.Controls.Add(sag, 1, 0);

            return headerPanel;
        }

        private void HeaderBilgileriniBagla(TableLayoutPanel anaYerlesim)
        {
            var icerikYerlesim = anaYerlesim.GetControlFromPosition(1, 0) as TableLayoutPanel;
            if (icerikYerlesim == null)
                return;

            var durumPanel = icerikYerlesim.GetControlFromPosition(0, 2) as Panel;
            if (durumPanel != null && durumPanel.Controls.Count > 0)
            {
                durumPanel.Controls.Clear();
                lblDurum.Text = "Hazir";
                lblDurum.Dock = DockStyle.Fill;
                lblDurum.ForeColor = Color.FromArgb(71, 85, 105);
                lblDurum.Font = new Font("Segoe UI", 9);
                durumPanel.Controls.Add(lblDurum);
            }

            var headerPanel = icerikYerlesim.GetControlFromPosition(0, 0) as Panel;
            if (headerPanel == null || headerPanel.Controls.Count == 0)
                return;

            var headerYerlesim = headerPanel.Controls[0] as TableLayoutPanel;
            if (headerYerlesim == null)
                return;

            var sagPanel = headerYerlesim.GetControlFromPosition(1, 0) as Panel;
            if (sagPanel == null)
                return;

            lblSaat.Left = 0;
            lblSaat.Top = 56;
            lblSaat.Width = 250;
            lblSaat.Height = 24;
            lblSaat.TextAlign = ContentAlignment.MiddleRight;
            lblSaat.ForeColor = Color.FromArgb(100, 116, 139);
            lblSaat.Font = new Font("Segoe UI", 9);
            sagPanel.Controls.Add(lblSaat);
        }

        private void BolumEkle(FlowLayoutPanel kartYerlesim, string baslik, string aciklama, params MenuEntry[] ekranlar)
        {
            var bolum = new Panel
            {
                Height = 190,
                Margin = new Padding(0, 0, 0, 18),
                BackColor = Color.Transparent
            };

            bolum.Controls.Add(new Label
            {
                Text = baslik,
                Left = 0,
                Top = 0,
                Width = 520,
                Height = 26,
                ForeColor = Color.FromArgb(15, 23, 42),
                Font = new Font("Segoe UI", 14, FontStyle.Bold)
            });

            bolum.Controls.Add(new Label
            {
                Text = aciklama,
                Left = 1,
                Top = 29,
                Width = 650,
                Height = 22,
                ForeColor = Color.FromArgb(100, 116, 139),
                Font = new Font("Segoe UI", 9)
            });

            var kartlar = new FlowLayoutPanel
            {
                Left = 0,
                Top = 58,
                Height = 124,
                FlowDirection = FlowDirection.LeftToRight,
                WrapContents = true,
                BackColor = Color.Transparent
            };

            foreach (var ekran in ekranlar)
                kartlar.Controls.Add(KartOlustur(ekran));

            bolum.Tag = kartlar;
            bolum.Controls.Add(kartlar);
            bolumPanelleri.Add(bolum);
            kartYerlesim.Controls.Add(bolum);
            BolumleriBoyutlandir();
        }

        private Control KartOlustur(MenuEntry ekran)
        {
            var kart = new CardPanel
            {
                Width = CardWidth,
                Height = CardHeight,
                Margin = new Padding(0, 0, 24, 18),
                BackColor = Color.White,
                BorderColor = Color.FromArgb(226, 232, 240),
                Cursor = Cursors.Hand
            };

            kart.Controls.Add(new Panel
            {
                Dock = DockStyle.Left,
                Width = 5,
                BackColor = ekran.Renk
            });

            kart.Controls.Add(new Label
            {
                Text = ekran.Baslik,
                Left = 22,
                Top = 17,
                Width = 206,
                Height = 24,
                ForeColor = Color.FromArgb(15, 23, 42),
                Font = new Font("Segoe UI", 11, FontStyle.Bold)
            });

            kart.Controls.Add(new Label
            {
                Text = ekran.Aciklama,
                Left = 22,
                Top = 45,
                Width = 208,
                Height = 38,
                ForeColor = Color.FromArgb(100, 116, 139),
                Font = new Font("Segoe UI", 9)
            });

            kart.Controls.Add(new Label
            {
                Text = ">",
                Left = 232,
                Top = 34,
                Width = 18,
                Height = 28,
                TextAlign = ContentAlignment.MiddleCenter,
                ForeColor = ekran.Renk,
                Font = new Font("Segoe UI", 14, FontStyle.Bold)
            });

            EventHandler tiklama = (sender, e) => EkranAc(ekran);
            EventHandler hover = (sender, e) =>
            {
                kart.BackColor = Color.FromArgb(248, 250, 252);
                kart.BorderColor = ekran.Renk;
                kart.Invalidate();
            };
            EventHandler normal = (sender, e) =>
            {
                kart.BackColor = Color.White;
                kart.BorderColor = Color.FromArgb(226, 232, 240);
                kart.Invalidate();
            };

            KartEventleriniBagla(kart, tiklama, hover, normal);
            return kart;
        }

        private Button SolMenuButonu(string text, Type formType)
        {
            return SolMenuButonu(text, formType, null);
        }

        private Button SolMenuButonu(string text, Type formType, EventHandler clickHandler)
        {
            var button = new Button
            {
                Text = text,
                Width = 198,
                Height = 42,
                Margin = new Padding(0, 0, 0, 10),
                TextAlign = ContentAlignment.MiddleLeft,
                Padding = new Padding(14, 0, 0, 0),
                FlatStyle = FlatStyle.Flat,
                BackColor = Color.FromArgb(30, 43, 61),
                ForeColor = Color.FromArgb(226, 232, 240),
                Font = new Font("Segoe UI", 10, FontStyle.Bold),
                Cursor = Cursors.Hand
            };
            button.FlatAppearance.BorderSize = 0;
            button.FlatAppearance.MouseOverBackColor = Color.FromArgb(45, 61, 83);
            button.FlatAppearance.MouseDownBackColor = Color.FromArgb(59, 130, 246);

            if (clickHandler != null)
                button.Click += clickHandler;
            else if (formType != null)
                button.Click += (sender, e) => EkranAc(new MenuEntry(text, "", formType, Color.FromArgb(59, 130, 246)));

            return button;
        }

        private void EkranAc(MenuEntry ekran)
        {
            try
            {
                foreach (Form acikForm in Application.OpenForms)
                {
                    if (acikForm.GetType() == ekran.FormType)
                    {
                        if (acikForm.WindowState == FormWindowState.Minimized)
                            acikForm.WindowState = FormWindowState.Normal;

                        acikForm.BringToFront();
                        acikForm.Activate();
                        lblDurum.Text = ekran.Baslik + " zaten acik, pencere one getirildi.";
                        return;
                    }
                }

                var form = (Form)Activator.CreateInstance(ekran.FormType);
                form.StartPosition = FormStartPosition.CenterScreen;
                form.Show(this);
                lblDurum.Text = ekran.Baslik + " acildi.";
            }
            catch (Exception ex)
            {
                MessageBox.Show(this, "Ekran acilirken hata olustu:" + Environment.NewLine + ex.Message, "Ekran Acilamadi", MessageBoxButtons.OK, MessageBoxIcon.Error);
                lblDurum.Text = ekran.Baslik + " acilamadi.";
            }
        }

        private void OturumuKapat(object sender, EventArgs e)
        {
            OturumKapatildi = true;
            Close();
        }

        private void KartEventleriniBagla(Control control, EventHandler tiklama, EventHandler hover, EventHandler normal)
        {
            control.Click += tiklama;
            control.MouseEnter += hover;
            control.MouseLeave += normal;

            foreach (Control child in control.Controls)
                KartEventleriniBagla(child, tiklama, hover, normal);
        }

        private void BolumleriBoyutlandir()
        {
            if (kartPaneli == null)
                return;

            int bolumGenisligi = Math.Max(360, kartPaneli.ClientSize.Width - 56);

            foreach (Panel bolum in bolumPanelleri)
            {
                var kartlar = bolum.Tag as FlowLayoutPanel;
                if (kartlar == null)
                    continue;

                bolum.Width = bolumGenisligi;
                kartlar.Width = bolumGenisligi;

                int satirdakiKart = Math.Max(1, bolumGenisligi / CardOuterWidth);
                int satirSayisi = (kartlar.Controls.Count + satirdakiKart - 1) / satirdakiKart;
                kartlar.Height = Math.Max(124, satirSayisi * (CardHeight + 18));
                bolum.Height = 62 + kartlar.Height + 8;
            }
        }

        private void SaatGuncelle()
        {
            lblSaat.Text = DateTime.Now.ToString("dd.MM.yyyy HH:mm:ss");
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing && saatTimer != null)
                saatTimer.Dispose();

            base.Dispose(disposing);
        }

        private sealed class MenuEntry
        {
            public MenuEntry(string baslik, string aciklama, Type formType, Color renk)
            {
                Baslik = baslik;
                Aciklama = aciklama;
                FormType = formType;
                Renk = renk;
            }

            public string Baslik { get; private set; }
            public string Aciklama { get; private set; }
            public Type FormType { get; private set; }
            public Color Renk { get; private set; }
        }

        private sealed class CardPanel : Panel
        {
            public Color BorderColor { get; set; }

            protected override void OnPaint(PaintEventArgs e)
            {
                base.OnPaint(e);

                using (var pen = new Pen(BorderColor))
                    e.Graphics.DrawRectangle(pen, 0, 0, Width - 1, Height - 1);
            }
        }
    }
}