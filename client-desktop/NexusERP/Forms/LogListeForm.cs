using System.Data;
using System.Windows.Forms;

namespace NexusERP
{
    public class LogListeForm : Form
    {
        private readonly DataGridView dgvLoglar;

        public LogListeForm()
        {
            Text = "Sistem Gunlukleri";
            Width = 900;
            Height = 520;
            StartPosition = FormStartPosition.CenterScreen;

            dgvLoglar = new DataGridView
            {
                Left = 30,
                Top = 30,
                Width = 820,
                Height = 400,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill
            };
            Controls.Add(dgvLoglar);

            Load += (sender, e) =>
            {
                LoglariListele();
                NexusERP.Helpers.ThemeHelper.ApplyTheme(this);
            };
        }

        private void LoglariListele()
        {
            dgvLoglar.DataSource = NexusERP.Services.LogService.LoglariGetir();
        }
    }
}