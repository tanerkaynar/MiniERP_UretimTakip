using System;
using System.Data;
using System.Data.SqlClient;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class CsvAktarimForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly DataGridView dgvRapor;
        private readonly Button btnRaporGetir;
        private readonly Button btnCsvAktar;

        public CsvAktarimForm()
        {
            Text = "Raporu CSV Olarak Aktar";
            Width = 1000;
            Height = 600;
            StartPosition = FormStartPosition.CenterScreen;

            btnRaporGetir = new Button
            {
                Text = "Raporu Getir",
                Left = 30,
                Top = 25,
                Width = 140,
                Height = 38
            };
            btnRaporGetir.Click += (sender, e) => RaporGetir();
            Controls.Add(btnRaporGetir);

            btnCsvAktar = new Button
            {
                Text = "CSV Aktar",
                Left = 190,
                Top = 25,
                Width = 140,
                Height = 38
            };
            btnCsvAktar.Click += (sender, e) => CsvAktar();
            Controls.Add(btnCsvAktar);

            dgvRapor = new DataGridView
            {
                Left = 30,
                Top = 85,
                Width = 900,
                Height = 410,
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
            using (var connection = new SqlConnection(ConnectionString))
            {
                var adapter = new SqlDataAdapter(@"
                    SELECT
                        u.UrunAdi,
                        m.MakineAdi,
                        p.AdSoyad AS PersonelAdi,
                        uk.UretimAdedi,
                        uk.UretimTarihi
                    FROM dbo.UretimKayitlari uk
                    INNER JOIN dbo.Urunler u ON uk.UrunID = u.UrunID
                    INNER JOIN dbo.Makineler m ON uk.MakineID = m.MakineID
                    INNER JOIN dbo.Personeller p ON uk.PersonelID = p.PersonelID
                    ORDER BY uk.UretimTarihi DESC", connection);

                var table = new DataTable();
                adapter.Fill(table);
                dgvRapor.DataSource = table;
            }
        }

        private void CsvAktar()
        {
            if (dgvRapor.Rows.Count == 0)
            {
                MessageBox.Show("Aktarilacak veri bulunamadi.");
                return;
            }

            using (var dialog = new SaveFileDialog())
            {
                dialog.Filter = "CSV Dosyasi (*.csv)|*.csv";
                dialog.FileName = "uretim_raporu.csv";

                if (dialog.ShowDialog() != DialogResult.OK)
                    return;

                var builder = new StringBuilder();

                for (int i = 0; i < dgvRapor.Columns.Count; i++)
                {
                    builder.Append(dgvRapor.Columns[i].HeaderText);
                    if (i < dgvRapor.Columns.Count - 1)
                        builder.Append(";");
                }
                builder.AppendLine();

                foreach (DataGridViewRow row in dgvRapor.Rows)
                {
                    if (row.IsNewRow)
                        continue;

                    for (int i = 0; i < dgvRapor.Columns.Count; i++)
                    {
                        var value = row.Cells[i].Value == null ? "" : row.Cells[i].Value.ToString();
                        builder.Append(value.Replace(";", ","));
                        if (i < dgvRapor.Columns.Count - 1)
                            builder.Append(";");
                    }
                    builder.AppendLine();
                }

                File.WriteAllText(dialog.FileName, builder.ToString(), Encoding.UTF8);
                MessageBox.Show("Rapor CSV dosyasina aktarildi.");
            }
        }
    }
}
