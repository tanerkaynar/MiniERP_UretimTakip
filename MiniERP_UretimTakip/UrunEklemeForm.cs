using System;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class UrunEklemeForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly TextBox txtUrunAdi;
        private readonly TextBox txtStokMiktari;
        private readonly TextBox txtBirimFiyat;
        private readonly Button btnKaydet;
        private readonly Button btnTemizle;
        private readonly DataGridView dgvUrunler;

        public UrunEklemeForm()
        {
            Text = "Urun Ekleme";
            Width = 950;
            Height = 600;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(Baslik());
            Controls.Add(Etiket("Urun Adi", 30, 80));
            txtUrunAdi = MetinKutusu(150, 76);
            Controls.Add(txtUrunAdi);

            Controls.Add(Etiket("Stok Miktari", 30, 125));
            txtStokMiktari = MetinKutusu(150, 121);
            Controls.Add(txtStokMiktari);

            Controls.Add(Etiket("Birim Fiyat", 30, 170));
            txtBirimFiyat = MetinKutusu(150, 166);
            Controls.Add(txtBirimFiyat);

            btnKaydet = Buton("Kaydet", 30, 225);
            btnTemizle = Buton("Temizle", 145, 225);
            btnKaydet.Click += BtnKaydet_Click;
            btnTemizle.Click += (sender, e) => FormuTemizle();
            Controls.Add(btnKaydet);
            Controls.Add(btnTemizle);

            dgvUrunler = new DataGridView
            {
                Left = 30,
                Top = 300,
                Width = 860,
                Height = 220,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false
            };
            Controls.Add(dgvUrunler);

            Load += (sender, e) => UrunleriListele();
        }

        private void BtnKaydet_Click(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(txtUrunAdi.Text))
            {
                MessageBox.Show("Urun adi bos olamaz.");
                return;
            }

            if (!int.TryParse(txtStokMiktari.Text, out int stokMiktari))
            {
                MessageBox.Show("Stok miktari sayisal olmalidir.");
                return;
            }

            if (!decimal.TryParse(txtBirimFiyat.Text, out decimal birimFiyat))
            {
                MessageBox.Show("Birim fiyat sayisal olmalidir.");
                return;
            }

            try
            {
                using (var connection = new SqlConnection(ConnectionString))
                using (var command = new SqlCommand(@"
                    INSERT INTO dbo.Urunler (UrunAdi, StokMiktari, BirimFiyat, AktifMi)
                    VALUES (@UrunAdi, @StokMiktari, @BirimFiyat, 1)", connection))
                {
                    command.Parameters.AddWithValue("@UrunAdi", txtUrunAdi.Text.Trim());
                    command.Parameters.AddWithValue("@StokMiktari", stokMiktari);
                    command.Parameters.AddWithValue("@BirimFiyat", birimFiyat);

                    connection.Open();
                    command.ExecuteNonQuery();
                }

                MessageBox.Show("Urun basariyla eklendi.");
                FormuTemizle();
                UrunleriListele();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Urun eklenirken hata olustu: " + ex.Message);
            }
        }

        private void UrunleriListele()
        {
            using (var connection = new SqlConnection(ConnectionString))
            {
                var adapter = new SqlDataAdapter(@"
                    SELECT UrunID, UrunAdi, StokMiktari, BirimFiyat, KayitTarihi
                    FROM dbo.Urunler
                    ORDER BY UrunID DESC", connection);
                var table = new DataTable();
                adapter.Fill(table);
                dgvUrunler.DataSource = table;
            }
        }

        private void FormuTemizle()
        {
            txtUrunAdi.Clear();
            txtStokMiktari.Clear();
            txtBirimFiyat.Clear();
            txtUrunAdi.Focus();
        }

        private Label Baslik()
        {
            return new Label
            {
                Text = "Urun Ekleme Formu",
                Left = 30,
                Top = 20,
                Width = 350,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            };
        }

        private Label Etiket(string text, int left, int top)
        {
            return new Label { Text = text, Left = left, Top = top, Width = 110, Height = 28, Font = new Font("Segoe UI", 10) };
        }

        private TextBox MetinKutusu(int left, int top)
        {
            return new TextBox { Left = left, Top = top, Width = 250, Height = 28, Font = new Font("Segoe UI", 10) };
        }

        private Button Buton(string text, int left, int top)
        {
            return new Button { Text = text, Left = left, Top = top, Width = 100, Height = 42, Font = new Font("Segoe UI", 10, FontStyle.Bold) };
        }
    }
}
