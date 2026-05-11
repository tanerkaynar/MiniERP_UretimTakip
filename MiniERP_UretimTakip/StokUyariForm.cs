using System;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class StokUyariForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly DataGridView dgvStok;
        private readonly Button btnYenile;

        public StokUyariForm()
        {
            Text = "Kritik Stok Uyarilari";
            Width = 950;
            Height = 580;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label
            {
                Text = "Stok Durumu",
                Left = 30,
                Top = 20,
                Width = 300,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            });

            btnYenile = new Button
            {
                Text = "Listeyi Yenile",
                Left = 30,
                Top = 75,
                Width = 140,
                Height = 36,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnYenile.Click += (sender, e) => StoklariListele();
            Controls.Add(btnYenile);

            dgvStok = new DataGridView
            {
                Left = 30,
                Top = 130,
                Width = 860,
                Height = 360,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false
            };
            dgvStok.DataBindingComplete += DgvStok_DataBindingComplete;
            Controls.Add(dgvStok);

            Load += (sender, e) => StoklariListele();
        }

        private void StoklariListele()
        {
            using (var connection = new SqlConnection(ConnectionString))
            {
                var adapter = new SqlDataAdapter(@"
                    SELECT
                        UrunID, UrunAdi, StokMiktari, BirimFiyat, StokMiktari * BirimFiyat AS StokDegeri,
                        CASE
                            WHEN StokMiktari <= 10 THEN 'Kritik'
                            WHEN StokMiktari <= 50 THEN 'Azaliyor'
                            ELSE 'Yeterli'
                        END AS StokDurumu
                    FROM dbo.Urunler
                    WHERE AktifMi = 1
                    ORDER BY StokMiktari ASC", connection);

                var table = new DataTable();
                adapter.Fill(table);
                dgvStok.DataSource = table;
            }
        }

        private void DgvStok_DataBindingComplete(object sender, DataGridViewBindingCompleteEventArgs e)
        {
            foreach (DataGridViewRow row in dgvStok.Rows)
            {
                var durum = Convert.ToString(row.Cells["StokDurumu"].Value);
                if (durum == "Kritik")
                {
                    row.DefaultCellStyle.BackColor = Color.LightCoral;
                    row.DefaultCellStyle.ForeColor = Color.Black;
                }
                else if (durum == "Azaliyor")
                {
                    row.DefaultCellStyle.BackColor = Color.Khaki;
                    row.DefaultCellStyle.ForeColor = Color.Black;
                }
                else
                {
                    row.DefaultCellStyle.BackColor = Color.Honeydew;
                    row.DefaultCellStyle.ForeColor = Color.Black;
                }
            }
        }
    }
}
