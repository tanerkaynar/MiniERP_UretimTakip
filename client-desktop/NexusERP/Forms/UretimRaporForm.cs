using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;

namespace NexusERP
{
    public class UretimRaporForm : Form
    {
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

            var btnCsvAktar = new Button
            {
                Text = "CSV Aktar",
                Left = 730,
                Top = 76,
                Width = 120,
                Height = 36,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnCsvAktar.Click += (sender, e) => CsvAktar();
            Controls.Add(btnCsvAktar);

            lblToplamUretim = new Label
            {
                Text = "Toplam Uretim: 0",
                Left = 860,
                Top = 82,
                Width = 170,
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
                NexusERP.Helpers.ThemeHelper.ApplyTheme(this);
            };
        }

        private void RaporuGetir()
        {
            try
            {
                DataTable table = NexusERP.Services.UretimService.TarihliUretimRaporuGetir(dtpBaslangic.Value, dtpBitis.Value);
                dgvRapor.DataSource = table;

                int toplam = 0;
                foreach (DataRow row in table.Rows)
                {
                    toplam += Convert.ToInt32(row["UretimAdedi"]);
                }

                lblToplamUretim.Text = "Toplam Uretim: " + toplam;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Rapor getirilirken hata olustu: " + ex.Message);
            }
        }

        private void CsvAktar()
        {
            if (dgvRapor.Rows.Count == 0)
            {
                MessageBox.Show("Aktarilacak veri bulunamadi.", "Bos Rapor", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            using (var dialog = new SaveFileDialog())
            {
                dialog.Filter = "CSV Dosyasi (*.csv)|*.csv";
                dialog.FileName = "uretim_raporu.csv";

                if (dialog.ShowDialog() != DialogResult.OK)
                    return;

                var builder = new System.Text.StringBuilder();

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

                System.IO.File.WriteAllText(dialog.FileName, builder.ToString(), System.Text.Encoding.UTF8);
                MessageBox.Show("Rapor CSV dosyasina aktarildi.", "Aktarim Basarili", MessageBoxButtons.OK, MessageBoxIcon.Information);
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