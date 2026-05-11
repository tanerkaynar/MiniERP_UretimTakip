using System;
using System.Data;
using System.Data.SqlClient;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class SevkiyatForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly DataGridView dgvSiparisler;
        private readonly Button btnSevkEt;
        private int seciliSiparisID;

        public SevkiyatForm()
        {
            Text = "Sevkiyat ve Stoktan Dusme";
            Width = 1000;
            Height = 560;
            StartPosition = FormStartPosition.CenterScreen;

            btnSevkEt = new Button { Text = "Secili Siparisi Sevk Et", Left = 30, Top = 25, Width = 190, Height = 40 };
            btnSevkEt.Click += BtnSevkEt_Click;
            Controls.Add(btnSevkEt);

            dgvSiparisler = new DataGridView
            {
                Left = 30,
                Top = 85,
                Width = 900,
                Height = 380,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect
            };
            dgvSiparisler.CellClick += DgvSiparisler_CellClick;
            Controls.Add(dgvSiparisler);

            Load += (sender, e) => SiparisleriListele();
        }

        private void DgvSiparisler_CellClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.RowIndex < 0)
                return;

            seciliSiparisID = Convert.ToInt32(dgvSiparisler.Rows[e.RowIndex].Cells["SiparisID"].Value);
        }

        private void BtnSevkEt_Click(object sender, EventArgs e)
        {
            if (seciliSiparisID == 0)
            {
                MessageBox.Show("Once bir siparis secmelisiniz.");
                return;
            }

            using (var connection = new SqlConnection(ConnectionString))
            {
                connection.Open();
                using (var transaction = connection.BeginTransaction())
                {
                    try
                    {
                        int urunID;
                        int miktar;
                        int mevcutStok;

                        using (var detayCommand = new SqlCommand(@"
                            SELECT TOP 1 UrunID, Miktar
                            FROM dbo.SiparisDetaylari
                            WHERE SiparisID = @SiparisID", connection, transaction))
                        {
                            detayCommand.Parameters.AddWithValue("@SiparisID", seciliSiparisID);
                            using (var reader = detayCommand.ExecuteReader())
                            {
                                if (!reader.Read())
                                    throw new Exception("Siparis detayi bulunamadi.");

                                urunID = Convert.ToInt32(reader["UrunID"]);
                                miktar = Convert.ToInt32(reader["Miktar"]);
                            }
                        }

                        using (var stokCommand = new SqlCommand("SELECT StokMiktari FROM dbo.Urunler WHERE UrunID = @UrunID", connection, transaction))
                        {
                            stokCommand.Parameters.AddWithValue("@UrunID", urunID);
                            mevcutStok = Convert.ToInt32(stokCommand.ExecuteScalar());
                        }

                        if (mevcutStok < miktar)
                            throw new Exception("Yetersiz stok. Mevcut stok: " + mevcutStok + ", istenen miktar: " + miktar);

                        using (var dusCommand = new SqlCommand(@"
                            UPDATE dbo.Urunler
                            SET StokMiktari = StokMiktari - @Miktar
                            WHERE UrunID = @UrunID", connection, transaction))
                        {
                            dusCommand.Parameters.AddWithValue("@Miktar", miktar);
                            dusCommand.Parameters.AddWithValue("@UrunID", urunID);
                            dusCommand.ExecuteNonQuery();
                        }

                        using (var durumCommand = new SqlCommand(@"
                            UPDATE dbo.Siparisler
                            SET Durum = N'Sevk Edildi'
                            WHERE SiparisID = @SiparisID", connection, transaction))
                        {
                            durumCommand.Parameters.AddWithValue("@SiparisID", seciliSiparisID);
                            durumCommand.ExecuteNonQuery();
                        }

                        transaction.Commit();
                        MessageBox.Show("Siparis sevk edildi ve stoktan dusuldu.");
                    }
                    catch (Exception ex)
                    {
                        transaction.Rollback();
                        MessageBox.Show("Sevkiyat yapilamadi: " + ex.Message);
                    }
                }
            }

            seciliSiparisID = 0;
            SiparisleriListele();
        }

        private void SiparisleriListele()
        {
            using (var connection = new SqlConnection(ConnectionString))
            {
                var adapter = new SqlDataAdapter(@"
                    SELECT
                        s.SiparisID,
                        m.MusteriAdi,
                        u.UrunAdi,
                        sd.Miktar,
                        ur.StokMiktari AS MevcutStok,
                        s.Durum,
                        s.SiparisTarihi
                    FROM dbo.Siparisler s
                    INNER JOIN dbo.Musteriler m ON s.MusteriID = m.MusteriID
                    INNER JOIN dbo.SiparisDetaylari sd ON s.SiparisID = sd.SiparisID
                    INNER JOIN dbo.Urunler u ON sd.UrunID = u.UrunID
                    INNER JOIN dbo.Urunler ur ON sd.UrunID = ur.UrunID
                    ORDER BY s.SiparisID DESC", connection);
                var table = new DataTable();
                adapter.Fill(table);
                dgvSiparisler.DataSource = table;
            }
        }
    }
}
