using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;

namespace NexusERP
{
    public class DurusAnalizForm : Form
    {
        private readonly DateTimePicker dtpBaslangic;
        private readonly DateTimePicker dtpBitis;
        private readonly Button btnAnalizEt;
        private readonly DataGridView dgvMakineler;

        public DurusAnalizForm()
        {
            Text = "Durus ve Makine Analizleri";
            Width = 900;
            Height = 560;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label
            {
                Text = "Uretim Yapmayan Durustaki Makineler",
                Left = 30,
                Top = 20,
                Width = 520,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            });

            Controls.Add(Etiket("Baslangic", 30, 82));
            dtpBaslangic = new DateTimePicker { Left = 130, Top = 78, Width = 170, Format = DateTimePickerFormat.Short };
            Controls.Add(dtpBaslangic);

            Controls.Add(Etiket("      Bitis", 275, 82));
            dtpBitis = new DateTimePicker { Left = 370, Top = 78, Width = 170, Format = DateTimePickerFormat.Short };
            Controls.Add(dtpBitis);

            btnAnalizEt = new Button
            {
                Text = "Analiz Et",
                Left = 570,
                Top = 75,
                Width = 120,
                Height = 36,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnAnalizEt.Click += (sender, e) => AnalizEt();
            Controls.Add(btnAnalizEt);

            dgvMakineler = new DataGridView
            {
                Left = 30,
                Top = 135,
                Width = 810,
                Height = 350,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false
            };
            Controls.Add(dgvMakineler);

            Load += (sender, e) =>
            {
                dtpBaslangic.Value = DateTime.Today.AddDays(-7);
                dtpBitis.Value = DateTime.Today;
                AnalizEt();
                NexusERP.Helpers.ThemeHelper.ApplyTheme(this);
            };
        }

        private void AnalizEt()
        {
            dgvMakineler.DataSource = NexusERP.Services.UretimService.DurusAnaliziGetir(dtpBaslangic.Value, dtpBitis.Value);
        }

        private Label Etiket(string text, int left, int top)
        {
            return new Label { Text = text, Left = left, Top = top, Width = 90, Height = 28, Font = new Font("Segoe UI", 10) };
        }
    }
}