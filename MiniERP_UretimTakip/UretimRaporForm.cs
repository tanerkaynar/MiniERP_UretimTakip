using System;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class UretimRaporForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly DateTimePicker dtpBaslangic;
        private readonly DateTimePicker dtpBitis;
        private readonly Button btnFiltrele;
        private readonly Label lblToplamUretim;
        private readonly DataGridView dgvRapor;

        public UretimRaporForm()
        {
            Text = "Tarih Araligina Gore Uretim Raporu";
            Width = 1050;
            Height = 620;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label
            {
                Text = "Uretim Raporu",
                Left = 30,
                Top = 20,
                Width = 300,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            });

            Controls.Add(Etiket("Baslangic", 30, 85));
            dtpBaslangic = new DateTimePicker
            {
                Left = 130,
                Top = 80,
                Width = 180,
                Format = DateTimePickerFormat.Short
            };
            Controls.Add(dtpBaslangic);

            Controls.Add(Etiket("  Bitis", 300, 85));
            dtpBitis = new DateTimePicker
            {
                Left = 390,
                Top = 80,
                Width = 180,
                Format = DateTimePickerFormat.Short
            };
            Controls.Add(dtpBitis);

            btnFiltrele = new Button
            {
                Text = "Filtrele",
                Left = 600,
                Top = 76,
                Width = 120,
                Height = 36,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnFiltrele.Click += (sender, e) => RaporuGetir();
            Controls.Add(btnFiltrele);

            lblToplamUretim = new Label
            {
                Text = "Toplam Uretim: 0",
                Left = 760,
                Top = 82,
                Width = 220,
                Height = 30,
                Font = new Font("Segoe UI", 11, FontStyle.Bold)
            };
            Controls.Add(lblToplamUretim);

            dgvRapor = new DataGridView
            {
                Left = 30,
                Top = 140,
                Width = 960,
                Height = 380,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false
            };
            Controls.Add(dgvRapor);

            Load += (sender, e) =>
            {
                dtpBaslangic.Value = DateTime.Today.AddDays(-7);
                dtpBitis.Value = DateTime.Today;
                RaporuGetir();
            };
        }

        private void RaporuGetir()
        {
            try
            {
                using (var connection = new SqlConnection(ConnectionString))
                using (var command = new SqlCommand(@"
                    SELECT
                        uk.UretimID,
                        u.UrunAdi,
                        m.MakineAdi,
                        p.AdSoyad AS PersonelAdi,
                        uk.UretimAdedi,
                        uk.UretimTarihi
                    FROM dbo.UretimKayitlari uk
                    INNER JOIN dbo.Urunler u ON uk.UrunID = u.UrunID
                    INNER JOIN dbo.Makineler m ON uk.MakineID = m.MakineID
                    INNER JOIN dbo.Personeller p ON uk.PersonelID = p.PersonelID
                    WHERE uk.UretimTarihi >= @Baslangic
                      AND uk.UretimTarihi < DATEADD(DAY, 1, @Bitis)
                    ORDER BY uk.UretimTarihi DESC", connection))
                {
                    command.Parameters.AddWithValue("@Baslangic", dtpBaslangic.Value.Date);
                    command.Parameters.AddWithValue("@Bitis", dtpBitis.Value.Date);

                    var adapter = new SqlDataAdapter(command);
                    var table = new DataTable();
                    adapter.Fill(table);
                    dgvRapor.DataSource = table;

                    int toplam = 0;
                    foreach (DataRow row in table.Rows)
                    {
                        toplam += Convert.ToInt32(row["UretimAdedi"]);
                    }

                    lblToplamUretim.Text = "Toplam Uretim: " + toplam;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Rapor getirilirken hata olustu: " + ex.Message);
            }
        }

        private Label Etiket(string text, int left, int top)
        {
            return new Label
            {
                Text = text,
                Left = left,
                Top = top,
                Width = 90,
                Height = 28,
                Font = new Font("Segoe UI", 10)
            };
        }
    }
}
