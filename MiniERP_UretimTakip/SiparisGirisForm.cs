using System;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class SiparisGirisForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly ComboBox cmbMusteriler;
        private readonly ComboBox cmbUrunler;
        private readonly TextBox txtMiktar;
        private readonly TextBox txtBirimFiyat;
        private readonly Button btnKaydet;
        private readonly DataGridView dgvSiparisler;

        public SiparisGirisForm()
        {
            Text = "Siparis Giris Formu";
            Width = 1000;
            Height = 620;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label { Text = "Siparis Girisi", Left = 30, Top = 20, Width = 300, Height = 35, Font = new Font("Segoe UI", 16, FontStyle.Bold) });

            Controls.Add(Etiket("Musteri", 30, 80));
            cmbMusteriler = Combo(150, 76);
            Controls.Add(cmbMusteriler);

            Controls.Add(Etiket("Urun", 30, 125));
            cmbUrunler = Combo(150, 121);
            Controls.Add(cmbUrunler);

            Controls.Add(Etiket("Miktar", 30, 170));
            txtMiktar = MetinKutusu(150, 166);
            Controls.Add(txtMiktar);

            Controls.Add(Etiket("Birim Fiyat", 30, 215));
            txtBirimFiyat = MetinKutusu(150, 211);
            Controls.Add(txtBirimFiyat);

            btnKaydet = new Button { Text = "Siparis Kaydet", Left = 150, Top = 260, Width = 160, Height = 42, Font = new Font("Segoe UI", 10, FontStyle.Bold) };
            btnKaydet.Click += BtnKaydet_Click;
            Controls.Add(btnKaydet);

            dgvSiparisler = new DataGridView { Left = 30, Top = 330, Width = 900, Height = 210, ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            Controls.Add(dgvSiparisler);

            Load += (sender, e) =>
            {
                MusterileriYukle();
                UrunleriYukle();
                SiparisleriListele();
            };
        }

        private void BtnKaydet_Click(object sender, EventArgs e)
        {
            if (!int.TryParse(txtMiktar.Text, out int miktar) || miktar <= 0)
            {
                MessageBox.Show("Miktar 0'dan buyuk sayi olmalidir.");
                return;
            }

            if (!decimal.TryParse(txtBirimFiyat.Text, out decimal birimFiyat) || birimFiyat < 0)
            {
                MessageBox.Show("Birim fiyat 0 veya daha buyuk sayi olmalidir.");
                return;
            }

            using (var connection = new SqlConnection(ConnectionString))
            {
                connection.Open();
                using (var transaction = connection.BeginTransaction())
                {
                    try
                    {
                        int siparisID;
                        using (var siparisCommand = new SqlCommand(@"
                            INSERT INTO dbo.Siparisler (MusteriID, SiparisTarihi, Durum)
                            VALUES (@MusteriID, GETDATE(), N'Hazirlaniyor');
                            SELECT SCOPE_IDENTITY();", connection, transaction))
                        {
                            siparisCommand.Parameters.AddWithValue("@MusteriID", Convert.ToInt32(cmbMusteriler.SelectedValue));
                            siparisID = Convert.ToInt32(siparisCommand.ExecuteScalar());
                        }

                        using (var detayCommand = new SqlCommand(@"
                            INSERT INTO dbo.SiparisDetaylari (SiparisID, UrunID, Miktar, BirimFiyat)
                            VALUES (@SiparisID, @UrunID, @Miktar, @BirimFiyat)", connection, transaction))
                        {
                            detayCommand.Parameters.AddWithValue("@SiparisID", siparisID);
                            detayCommand.Parameters.AddWithValue("@UrunID", Convert.ToInt32(cmbUrunler.SelectedValue));
                            detayCommand.Parameters.AddWithValue("@Miktar", miktar);
                            detayCommand.Parameters.AddWithValue("@BirimFiyat", birimFiyat);
                            detayCommand.ExecuteNonQuery();
                        }

                        transaction.Commit();
                        MessageBox.Show("Siparis kaydedildi.");
                    }
                    catch (Exception ex)
                    {
                        transaction.Rollback();
                        MessageBox.Show("Siparis kaydedilirken hata olustu: " + ex.Message);
                    }
                }
            }

            SiparisleriListele();
        }

        private void MusterileriYukle()
        {
            cmbMusteriler.DataSource = TabloGetir("SELECT MusteriID, MusteriAdi FROM dbo.Musteriler ORDER BY MusteriAdi");
            cmbMusteriler.DisplayMember = "MusteriAdi";
            cmbMusteriler.ValueMember = "MusteriID";
        }

        private void UrunleriYukle()
        {
            cmbUrunler.DataSource = TabloGetir("SELECT UrunID, UrunAdi FROM dbo.Urunler WHERE AktifMi = 1 ORDER BY UrunAdi");
            cmbUrunler.DisplayMember = "UrunAdi";
            cmbUrunler.ValueMember = "UrunID";
        }

        private void SiparisleriListele()
        {
            dgvSiparisler.DataSource = TabloGetir(@"
                SELECT TOP 50
                    s.SiparisID,
                    m.MusteriAdi,
                    u.UrunAdi,
                    sd.Miktar,
                    sd.BirimFiyat,
                    s.Durum,
                    s.SiparisTarihi
                FROM dbo.Siparisler s
                INNER JOIN dbo.Musteriler m ON s.MusteriID = m.MusteriID
                INNER JOIN dbo.SiparisDetaylari sd ON s.SiparisID = sd.SiparisID
                INNER JOIN dbo.Urunler u ON sd.UrunID = u.UrunID
                ORDER BY s.SiparisID DESC");
        }

        private DataTable TabloGetir(string query)
        {
            using (var connection = new SqlConnection(ConnectionString))
            {
                var adapter = new SqlDataAdapter(query, connection);
                var table = new DataTable();
                adapter.Fill(table);
                return table;
            }
        }

        private Label Etiket(string text, int left, int top) => new Label { Text = text, Left = left, Top = top, Width = 110, Height = 28, Font = new Font("Segoe UI", 10) };
        private ComboBox Combo(int left, int top) => new ComboBox { Left = left, Top = top, Width = 280, DropDownStyle = ComboBoxStyle.DropDownList, Font = new Font("Segoe UI", 10) };
        private TextBox MetinKutusu(int left, int top) => new TextBox { Left = left, Top = top, Width = 160, Font = new Font("Segoe UI", 10) };
    }
}
