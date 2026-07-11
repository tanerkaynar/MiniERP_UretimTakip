using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;

namespace NexusERP
{
    public class GrupluRaporForm : Form
    {
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

            Load += (sender, e) =>
            {
                RaporGetir();
                NexusERP.Helpers.ThemeHelper.ApplyTheme(this);
            };
        }

        private void RaporGetir()
        {
            dgvRapor.DataSource = NexusERP.Services.UretimService.GrupluRaporGetir(cmbRaporTuru.SelectedItem.ToString());
        }
    }
}