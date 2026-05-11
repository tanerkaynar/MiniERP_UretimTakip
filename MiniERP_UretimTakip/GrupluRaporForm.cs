using System;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class GrupluRaporForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly ComboBox cmbRaporTuru;
        private readonly Button btnRaporGetir;
        private readonly DataGridView dgvRapor;

        public GrupluRaporForm()
        {
            Text = "Gruplu Uretim Raporlari";
            Width = 950;
            Height = 580;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label
            {
                Text = "Gruplu Uretim Raporlari",
                Left = 30,
                Top = 20,
                Width = 400,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            });

            Controls.Add(new Label
            {
                Text = "Rapor Turu",
                Left = 30,
                Top = 82,
                Width = 100,
                Height = 28,
                Font = new Font("Segoe UI", 10)
            });

            cmbRaporTuru = new ComboBox
            {
                Left = 140,
                Top = 78,
                Width = 240,
                Height = 30,
                DropDownStyle = ComboBoxStyle.DropDownList
            };
            cmbRaporTuru.Items.Add("Makine Bazli");
            cmbRaporTuru.Items.Add("Urun Bazli");
            cmbRaporTuru.Items.Add("Personel Bazli");
            cmbRaporTuru.SelectedIndex = 0;
            Controls.Add(cmbRaporTuru);

            btnRaporGetir = new Button
            {
                Text = "Rapor Getir",
                Left = 410,
                Top = 75,
                Width = 130,
                Height = 36,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnRaporGetir.Click += (sender, e) => RaporGetir();
            Controls.Add(btnRaporGetir);

            dgvRapor = new DataGridView
            {
                Left = 30,
                Top = 140,
                Width = 860,
                Height = 360,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false
            };
            Controls.Add(dgvRapor);

            Load += (sender, e) => RaporGetir();
        }

        private void RaporGetir()
        {
            string query;
            if (cmbRaporTuru.SelectedItem.ToString() == "Urun Bazli")
            {
                query = @"
                    SELECT
                        u.UrunAdi,
                        COUNT(uk.UretimID) AS UretimKaydiSayisi,
                        SUM(uk.UretimAdedi) AS ToplamUretimAdedi
                    FROM dbo.UretimKayitlari uk
                    INNER JOIN dbo.Urunler u ON uk.UrunID = u.UrunID
                    GROUP BY u.UrunAdi
                    ORDER BY ToplamUretimAdedi DESC";
            }
            else if (cmbRaporTuru.SelectedItem.ToString() == "Personel Bazli")
            {
                query = @"
                    SELECT
                        p.AdSoyad AS PersonelAdi,
                        COUNT(uk.UretimID) AS UretimKaydiSayisi,
                        SUM(uk.UretimAdedi) AS ToplamUretimAdedi
                    FROM dbo.UretimKayitlari uk
                    INNER JOIN dbo.Personeller p ON uk.PersonelID = p.PersonelID
                    GROUP BY p.AdSoyad
                    ORDER BY ToplamUretimAdedi DESC";
            }
            else
            {
                query = @"
                    SELECT
                        m.MakineAdi,
                        COUNT(uk.UretimID) AS UretimKaydiSayisi,
                        SUM(uk.UretimAdedi) AS ToplamUretimAdedi,
                        AVG(CAST(uk.UretimAdedi AS DECIMAL(18,2))) AS OrtalamaUretim
                    FROM dbo.UretimKayitlari uk
                    INNER JOIN dbo.Makineler m ON uk.MakineID = m.MakineID
                    GROUP BY m.MakineAdi
                    ORDER BY ToplamUretimAdedi DESC";
            }

            using (var connection = new SqlConnection(ConnectionString))
            {
                var adapter = new SqlDataAdapter(query, connection);
                var table = new DataTable();
                adapter.Fill(table);
                dgvRapor.DataSource = table;
            }
        }
    }
}
