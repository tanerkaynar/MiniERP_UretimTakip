using System;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class UrunListelemeForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly DataGridView dgvUrunler;
        private readonly Button btnListele;

        public UrunListelemeForm()
        {
            Text = "DataGridView ile Urun Listeleme";
            Width = 900;
            Height = 520;
            StartPosition = FormStartPosition.CenterScreen;

            btnListele = new Button
            {
                Text = "Listeyi Yenile",
                Left = 30,
                Top = 25,
                Width = 150,
                Height = 40,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };

            dgvUrunler = new DataGridView
            {
                Left = 30,
                Top = 85,
                Width = 810,
                Height = 340,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false
            };

            btnListele.Click += (sender, e) => UrunleriListele();

            Controls.Add(btnListele);
            Controls.Add(dgvUrunler);

            Load += (sender, e) => UrunleriListele();
        }

        private void UrunleriListele()
        {
            try
            {
                using (var connection = new SqlConnection(ConnectionString))
                {
                    const string query = @"
                        SELECT
                            UrunID,
                            UrunAdi AS [Urun Adi],
                            StokMiktari AS [Stok Miktari],
                            BirimFiyat AS [Birim Fiyat],
                            CASE WHEN AktifMi = 1 THEN 'Aktif' ELSE 'Pasif' END AS [Durum],
                            KayitTarihi AS [Kayit Tarihi]
                        FROM dbo.Urunler
                        ORDER BY UrunID DESC";

                    var adapter = new SqlDataAdapter(query, connection);
                    var table = new DataTable();
                    adapter.Fill(table);
                    dgvUrunler.DataSource = table;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Urunler listelenirken hata olustu: " + ex.Message);
            }
        }
    }
}
