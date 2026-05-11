using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class UrunFormTaslak : Form
    {
        private readonly TextBox txtUrunAdi;
        private readonly TextBox txtStokMiktari;
        private readonly TextBox txtBirimFiyat;
        private readonly Button btnKaydet;
        private readonly Button btnGuncelle;
        private readonly Button btnSil;
        private readonly Button btnTemizle;
        private readonly DataGridView dgvUrunler;

        public UrunFormTaslak()
        {
            Text = "Urun Islemleri - Taslak Form";
            Width = 950;
            Height = 600;
            StartPosition = FormStartPosition.CenterScreen;

            var lblBaslik = new Label
            {
                Text = "Urun Islemleri",
                Left = 30,
                Top = 20,
                Width = 300,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            };

            var lblUrunAdi = Etiket("Urun Adi", 30, 80);
            txtUrunAdi = MetinKutusu(140, 76);

            var lblStok = Etiket("Stok Miktari", 30, 125);
            txtStokMiktari = MetinKutusu(140, 121);

            var lblFiyat = Etiket("Birim Fiyat", 30, 170);
            txtBirimFiyat = MetinKutusu(140, 166);

            btnKaydet = Buton("Kaydet", 30, 225);
            btnGuncelle = Buton("Guncelle", 145, 225);
            btnSil = Buton("Sil", 260, 225);
            btnTemizle = Buton("Temizle", 375, 225);

            dgvUrunler = new DataGridView
            {
                Left = 30,
                Top = 300,
                Width = 860,
                Height = 220,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect
            };

            Controls.Add(lblBaslik);
            Controls.Add(lblUrunAdi);
            Controls.Add(txtUrunAdi);
            Controls.Add(lblStok);
            Controls.Add(txtStokMiktari);
            Controls.Add(lblFiyat);
            Controls.Add(txtBirimFiyat);
            Controls.Add(btnKaydet);
            Controls.Add(btnGuncelle);
            Controls.Add(btnSil);
            Controls.Add(btnTemizle);
            Controls.Add(dgvUrunler);
        }

        private Label Etiket(string text, int left, int top)
        {
            return new Label
            {
                Text = text,
                Left = left,
                Top = top,
                Width = 100,
                Height = 28,
                Font = new Font("Segoe UI", 10)
            };
        }

        private TextBox MetinKutusu(int left, int top)
        {
            return new TextBox
            {
                Left = left,
                Top = top,
                Width = 250,
                Height = 28,
                Font = new Font("Segoe UI", 10)
            };
        }

        private Button Buton(string text, int left, int top)
        {
            return new Button
            {
                Text = text,
                Left = left,
                Top = top,
                Width = 100,
                Height = 42,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
        }
    }
}
