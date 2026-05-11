using System;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class UrunCrudForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private int seciliUrunID = 0;
        private readonly TextBox txtUrunAdi;
        private readonly TextBox txtStokMiktari;
        private readonly TextBox txtBirimFiyat;
        private readonly Button btnKaydet;
        private readonly Button btnGuncelle;
        private readonly Button btnSil;
        private readonly Button btnTemizle;
        private readonly DataGridView dgvUrunler;

        public UrunCrudForm()
        {
            Text = "Urun CRUD Islemleri";
            Width = 980;
            Height = 620;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label
            {
                Text = "Urun Kayit, Guncelleme ve Silme",
                Left = 30,
                Top = 20,
                Width = 500,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            });

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
            btnGuncelle = Buton("Guncelle", 145, 225);
            btnSil = Buton("Sil", 260, 225);
            btnTemizle = Buton("Temizle", 375, 225);

            btnKaydet.Click += BtnKaydet_Click;
            btnGuncelle.Click += BtnGuncelle_Click;
            btnSil.Click += BtnSil_Click;
            btnTemizle.Click += (sender, e) => FormuTemizle();

            Controls.Add(btnKaydet);
            Controls.Add(btnGuncelle);
            Controls.Add(btnSil);
            Controls.Add(btnTemizle);

            dgvUrunler = new DataGridView
            {
                Left = 30,
                Top = 300,
                Width = 890,
                Height = 240,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false
            };
            dgvUrunler.CellClick += DgvUrunler_CellClick;
            Controls.Add(dgvUrunler);

            Load += (sender, e) => UrunleriListele();
        }

        private void BtnKaydet_Click(object sender, EventArgs e)
        {
            if (!FormGecerliMi(out int stokMiktari, out decimal birimFiyat))
                return;

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

                MessageBox.Show("Urun kaydedildi.");
                FormuTemizle();
                UrunleriListele();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Kayit sirasinda hata olustu: " + ex.Message);
            }
        }

        private void BtnGuncelle_Click(object sender, EventArgs e)
        {
            if (seciliUrunID == 0)
            {
                MessageBox.Show("Once listeden bir urun secmelisiniz.");
                return;
            }

            if (!FormGecerliMi(out int stokMiktari, out decimal birimFiyat))
                return;

            try
            {
                using (var connection = new SqlConnection(ConnectionString))
                using (var command = new SqlCommand(@"
                    UPDATE dbo.Urunler
                    SET UrunAdi = @UrunAdi, StokMiktari = @StokMiktari, BirimFiyat = @BirimFiyat
                    WHERE UrunID = @UrunID", connection))
                {
                    command.Parameters.AddWithValue("@UrunID", seciliUrunID);
                    command.Parameters.AddWithValue("@UrunAdi", txtUrunAdi.Text.Trim());
                    command.Parameters.AddWithValue("@StokMiktari", stokMiktari);
                    command.Parameters.AddWithValue("@BirimFiyat", birimFiyat);
                    connection.Open();
                    command.ExecuteNonQuery();
                }

                MessageBox.Show("Urun guncellendi.");
                FormuTemizle();
                UrunleriListele();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Guncelleme sirasinda hata olustu: " + ex.Message);
            }
        }

        private void BtnSil_Click(object sender, EventArgs e)
        {
            if (seciliUrunID == 0)
            {
                MessageBox.Show("Once listeden bir urun secmelisiniz.");
                return;
            }

            var sonuc = MessageBox.Show("Secili urun silinsin mi?", "Silme Onayi", MessageBoxButtons.YesNo);
            if (sonuc != DialogResult.Yes)
                return;

            try
            {
                using (var connection = new SqlConnection(ConnectionString))
                using (var command = new SqlCommand("DELETE FROM dbo.Urunler WHERE UrunID = @UrunID", connection))
                {
                    command.Parameters.AddWithValue("@UrunID", seciliUrunID);
                    connection.Open();
                    command.ExecuteNonQuery();
                }

                MessageBox.Show("Urun silindi.");
                FormuTemizle();
                UrunleriListele();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Silme sirasinda hata olustu: " + ex.Message);
            }
        }

        private void DgvUrunler_CellClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.RowIndex < 0)
                return;

            var row = dgvUrunler.Rows[e.RowIndex];
            seciliUrunID = Convert.ToInt32(row.Cells["UrunID"].Value);
            txtUrunAdi.Text = row.Cells["UrunAdi"].Value.ToString();
            txtStokMiktari.Text = row.Cells["StokMiktari"].Value.ToString();
            txtBirimFiyat.Text = row.Cells["BirimFiyat"].Value.ToString();
        }

        private bool FormGecerliMi(out int stokMiktari, out decimal birimFiyat)
        {
            stokMiktari = 0;
            birimFiyat = 0;

            if (string.IsNullOrWhiteSpace(txtUrunAdi.Text))
            {
                MessageBox.Show("Urun adi bos olamaz.");
                return false;
            }

            if (!int.TryParse(txtStokMiktari.Text, out stokMiktari) || stokMiktari < 0)
            {
                MessageBox.Show("Stok miktari 0 veya daha buyuk sayi olmalidir.");
                return false;
            }

            if (!decimal.TryParse(txtBirimFiyat.Text, out birimFiyat) || birimFiyat < 0)
            {
                MessageBox.Show("Birim fiyat 0 veya daha buyuk sayi olmalidir.");
                return false;
            }

            return true;
        }

        private void UrunleriListele()
        {
            using (var connection = new SqlConnection(ConnectionString))
            {
                var adapter = new SqlDataAdapter(@"
                    SELECT UrunID, UrunAdi, StokMiktari, BirimFiyat, AktifMi, KayitTarihi
                    FROM dbo.Urunler
                    ORDER BY UrunID DESC", connection);
                var table = new DataTable();
                adapter.Fill(table);
                dgvUrunler.DataSource = table;
            }
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
