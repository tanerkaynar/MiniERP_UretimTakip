using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;
using NexusERP.Services;

namespace NexusERP
{
    public class UrunYonetimiForm : Form
    {
        private int seciliUrunID = 0;
        private readonly TextBox txtUrunAdi;
        private readonly TextBox txtStokMiktari;
        private readonly TextBox txtBirimFiyat;
        private readonly Button btnKaydet;
        private readonly Button btnGuncelle;
        private readonly Button btnSil;
        private readonly Button btnTemizle;
        private readonly DataGridView dgvUrunler;

        public UrunYonetimiForm()
        {
            Text = "Urun Yonetim Paneli";
            Width = 980;
            Height = 620;
            StartPosition = FormStartPosition.CenterScreen;
            BackColor = Color.FromArgb(244, 247, 251);
            Font = new Font("Segoe UI", 10);

            var baslikLabel = new Label
            {
                Text = "Urun Yonetim Paneli",
                Left = 30,
                Top = 20,
                Width = 500,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold),
                ForeColor = Color.FromArgb(17, 24, 39)
            };
            Controls.Add(baslikLabel);

            var inputPanel = new Panel
            {
                Left = 30,
                Top = 75,
                Width = 380,
                Height = 190,
                BackColor = Color.White,
                Padding = new Padding(15)
            };
            Controls.Add(inputPanel);

            inputPanel.Controls.Add(new Label { Text = "Urun Adi", Left = 15, Top = 20, Width = 100, Font = new Font("Segoe UI", 10, FontStyle.Bold) });
            txtUrunAdi = new TextBox { Left = 120, Top = 16, Width = 230, Font = new Font("Segoe UI", 10) };
            inputPanel.Controls.Add(txtUrunAdi);

            inputPanel.Controls.Add(new Label { Text = "Stok Miktari", Left = 15, Top = 65, Width = 100, Font = new Font("Segoe UI", 10, FontStyle.Bold) });
            txtStokMiktari = new TextBox { Left = 120, Top = 61, Width = 230, Font = new Font("Segoe UI", 10) };
            inputPanel.Controls.Add(txtStokMiktari);

            inputPanel.Controls.Add(new Label { Text = "Birim Fiyat", Left = 15, Top = 110, Width = 100, Font = new Font("Segoe UI", 10, FontStyle.Bold) });
            txtBirimFiyat = new TextBox { Left = 120, Top = 106, Width = 230, Font = new Font("Segoe UI", 10) };
            inputPanel.Controls.Add(txtBirimFiyat);

            var buttonPanel = new FlowLayoutPanel
            {
                Left = 30,
                Top = 280,
                Width = 380,
                Height = 110,
                FlowDirection = FlowDirection.LeftToRight,
                BackColor = Color.Transparent
            };
            Controls.Add(buttonPanel);

            btnKaydet = Buton("Urun Ekle", Color.FromArgb(34, 197, 94), Color.White);
            btnGuncelle = Buton("Guncelle", Color.FromArgb(59, 130, 246), Color.White);
            btnSil = Buton("Sil", Color.FromArgb(239, 68, 68), Color.White);
            btnTemizle = Buton("Temizle", Color.White, Color.FromArgb(75, 85, 99));
            btnTemizle.FlatAppearance.BorderColor = Color.FromArgb(209, 213, 219);

            btnKaydet.Click += BtnKaydet_Click;
            btnGuncelle.Click += BtnGuncelle_Click;
            btnSil.Click += BtnSil_Click;
            btnTemizle.Click += (sender, e) => FormuTemizle();

            buttonPanel.Controls.Add(btnKaydet);
            buttonPanel.Controls.Add(btnGuncelle);
            buttonPanel.Controls.Add(btnSil);
            buttonPanel.Controls.Add(btnTemizle);

            dgvUrunler = new DataGridView
            {
                Left = 440,
                Top = 75,
                Width = 500,
                Height = 480,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false,
                BackgroundColor = Color.White,
                BorderStyle = BorderStyle.None,
                RowHeadersVisible = false
            };
            dgvUrunler.CellClick += DgvUrunler_CellClick;
            Controls.Add(dgvUrunler);

            Load += (sender, e) =>
            {
                UrunleriListele();
                NexusERP.Helpers.ThemeHelper.ApplyTheme(this);
            };
        }

        private void BtnKaydet_Click(object sender, EventArgs e)
        {
            if (!FormGecerliMi(out int stokMiktari, out decimal birimFiyat))
                return;

            try
            {
                UrunService.UrunEkle(txtUrunAdi.Text.Trim(), stokMiktari, birimFiyat);
                MessageBox.Show("Yeni urun basariyla eklendi.", "Basarili", MessageBoxButtons.OK, MessageBoxIcon.Information);
                FormuTemizle();
                UrunleriListele();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Kayit sirasinda hata olustu: " + ex.Message, "Hata", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void BtnGuncelle_Click(object sender, EventArgs e)
        {
            if (seciliUrunID == 0)
            {
                MessageBox.Show("Lutfen listeden guncellemek istediginiz urunu secin.", "Urun Secilmedi", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            if (!FormGecerliMi(out int stokMiktari, out decimal birimFiyat))
                return;

            try
            {
                UrunService.UrunGuncelle(seciliUrunID, txtUrunAdi.Text.Trim(), stokMiktari, birimFiyat);
                MessageBox.Show("Urun bilgileri guncellendi.", "Basarili", MessageBoxButtons.OK, MessageBoxIcon.Information);
                FormuTemizle();
                UrunleriListele();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Guncelleme sirasinda hata olustu: " + ex.Message, "Hata", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void BtnSil_Click(object sender, EventArgs e)
        {
            if (seciliUrunID == 0)
            {
                MessageBox.Show("Lutfen silmek istediginiz urunu listeden secin.", "Urun Secilmedi", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            var sonuc = MessageBox.Show("Secili urunu silmek istediginize emin misiniz?", "Silme Onayi", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            if (sonuc != DialogResult.Yes)
                return;

            try
            {
                UrunService.UrunSil(seciliUrunID);
                MessageBox.Show("Urun basariyla silindi.", "Basarili", MessageBoxButtons.OK, MessageBoxIcon.Information);
                FormuTemizle();
                UrunleriListele();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Silme sirasinda hata olustu: " + ex.Message, "Hata", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void DgvUrunler_CellClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.RowIndex < 0)
                return;

            var row = dgvUrunler.Rows[e.RowIndex];
            seciliUrunID = Convert.ToInt32(row.Cells["urunid"].Value);
            txtUrunAdi.Text = row.Cells["urunadi"].Value.ToString();
            txtStokMiktari.Text = row.Cells["stokmiktari"].Value.ToString();
            txtBirimFiyat.Text = row.Cells["birimfiyat"].Value.ToString();
        }

        private bool FormGecerliMi(out int stokMiktari, out decimal birimFiyat)
        {
            stokMiktari = 0;
            birimFiyat = 0;

            if (string.IsNullOrWhiteSpace(txtUrunAdi.Text))
            {
                MessageBox.Show("Urun adi bos olamaz.", "Hata", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return false;
            }

            if (!int.TryParse(txtStokMiktari.Text, out stokMiktari) || stokMiktari < 0)
            {
                MessageBox.Show("Stok miktari sifir veya daha buyuk bir tam sayi olmalidir.", "Hata", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return false;
            }

            if (!decimal.TryParse(txtBirimFiyat.Text, out birimFiyat) || birimFiyat < 0)
            {
                MessageBox.Show("Birim fiyat sifir veya daha buyuk bir sayi olmalidir.", "Hata", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return false;
            }

            return true;
        }

        private void UrunleriListele()
        {
            dgvUrunler.DataSource = UrunService.TumUrunleriGetir();
            if (dgvUrunler.Columns["aktifmi"] != null)
                dgvUrunler.Columns["aktifmi"].Visible = false;
        }

        private void FormuTemizle()
        {
            seciliUrunID = 0;
            txtUrunAdi.Clear();
            txtStokMiktari.Clear();
            txtBirimFiyat.Clear();
            dgvUrunler.ClearSelection();
            txtUrunAdi.Focus();
        }

        private Button Buton(string text, Color backColor, Color foreColor)
        {
            return new Button
            {
                Text = text,
                Width = 170,
                Height = 42,
                Margin = new Padding(5),
                BackColor = backColor,
                ForeColor = foreColor,
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
        }
    }
}