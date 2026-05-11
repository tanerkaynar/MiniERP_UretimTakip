using System;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class FrmAnaMenu : Form
    {
        public FrmAnaMenu()
        {
            Text = "Mini ERP - Uretim Takip Sistemi";
            Width = 900;
            Height = 550;
            StartPosition = FormStartPosition.CenterScreen;

            var baslik = new Label
            {
                Text = "Mini ERP / Uretim Takip Sistemi",
                AutoSize = false,
                Width = 820,
                Height = 50,
                Left = 30,
                Top = 25,
                Font = new Font("Segoe UI", 18, FontStyle.Bold)
            };

            var aciklama = new Label
            {
                Text = "Bu ekran proje modullerine gecis icin hazirlanan ana menudur.",
                AutoSize = false,
                Width = 820,
                Height = 35,
                Left = 30,
                Top = 80,
                Font = new Font("Segoe UI", 10)
            };

            var btnUrunler = MenuButonu("Urun Islemleri", 30, 140);
            var btnUretim = MenuButonu("Uretim Girisi", 250, 140);
            var btnRaporlar = MenuButonu("Raporlar", 470, 140);

            btnUrunler.Click += (sender, e) => MessageBox.Show("Urun formu sonraki gunlerde acilacak.");
            btnUretim.Click += (sender, e) => MessageBox.Show("Uretim formu sonraki gunlerde acilacak.");
            btnRaporlar.Click += (sender, e) => MessageBox.Show("Rapor ekrani sonraki gunlerde acilacak.");

            Controls.Add(baslik);
            Controls.Add(aciklama);
            Controls.Add(btnUrunler);
            Controls.Add(btnUretim);
            Controls.Add(btnRaporlar);
        }

        private Button MenuButonu(string text, int left, int top)
        {
            return new Button
            {
                Text = text,
                Left = left,
                Top = top,
                Width = 180,
                Height = 60,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
        }
    }
}
