using System;
using System.Data;
using System.Windows.Forms;

namespace NexusERP
{
    public class SevkiyatForm : Form
    {
        private readonly DataGridView dgvSiparisler;
        private readonly Button btnSevkEt;
        private int seciliSiparisID;

        public SevkiyatForm()
        {
            Text = "Sevkiyat Yonetim Paneli";
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

            Load += (sender, e) =>
            {
                SiparisleriListele();
                NexusERP.Helpers.ThemeHelper.ApplyTheme(this);
            };
        }

        private void DgvSiparisler_CellClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.RowIndex < 0 || dgvSiparisler.Rows[e.RowIndex].IsNewRow)
                return;

            var cellValue = dgvSiparisler.Rows[e.RowIndex].Cells["siparisid"].Value;
            if (cellValue != null && cellValue != DBNull.Value)
            {
                seciliSiparisID = Convert.ToInt32(cellValue);
            }
        }

        private void BtnSevkEt_Click(object sender, EventArgs e)
        {
            if (seciliSiparisID == 0)
            {
                MessageBox.Show("Once bir siparis secmelisiniz.");
                return;
            }

            try
            {
                NexusERP.Services.SiparisService.SevkEt(seciliSiparisID);
                MessageBox.Show("Siparis sevk edildi ve stoktan dusuldu.");
            }
            catch (Exception ex)
            {
                MessageBox.Show("Sevkiyat yapilamadi: " + ex.Message);
            }

            seciliSiparisID = 0;
            SiparisleriListele();
        }

        private void SiparisleriListele()
        {
            dgvSiparisler.DataSource = NexusERP.Services.SiparisService.SevkiyatBekleyenSiparisleriGetir();
        }
    }
}