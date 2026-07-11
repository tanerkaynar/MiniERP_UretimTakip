using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;
using NexusERP.Services;
using NexusERP.Helpers;

namespace NexusERP
{
    public class StokTakipForm : Form
    {
        private readonly DataGridView dgvStok;
        private readonly Button btnYenile;
        private readonly ComboBox cmbUrunler;
        private readonly TextBox txtMiktar;
        private readonly Label lblMevcutStok;
        private readonly Button btnKontrolEt;

        public StokTakipForm()
        {
            Text = "Stok Takip & Kontrol Paneli";
            Width = 1000;
            Height = 600;
            StartPosition = FormStartPosition.CenterScreen;
            BackColor = Color.FromArgb(244, 247, 251);
            Font = new Font("Segoe UI", 10);

            var baslikLabel = new Label
            {
                Text = "Stok Durum & Yeterlilik Kontrolu",
                Left = 30,
                Top = 20,
                Width = 500,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold),
                ForeColor = Color.FromArgb(17, 24, 39)
            };
            Controls.Add(baslikLabel);

            var checkPanel = new Panel
            {
                Left = 30,
                Top = 85,
                Width = 350,
                Height = 260,
                BackColor = Color.White,
                Padding = new Padding(15)
            };
            Controls.Add(checkPanel);

            var checkHeader = new Label
            {
                Text = "Stok Yeterlilik Testi",
                Left = 15,
                Top = 15,
                Width = 320,
                Height = 25,
                Font = new Font("Segoe UI", 12, FontStyle.Bold),
                ForeColor = Color.FromArgb(31, 41, 55)
            };
            checkPanel.Controls.Add(checkHeader);

            checkPanel.Controls.Add(new Label { Text = "Urun Secin", Left = 15, Top = 55, Width = 320, Font = new Font("Segoe UI", 10, FontStyle.Bold) });
            cmbUrunler = new ComboBox
            {
                Left = 15,
                Top = 80,
                Width = 320,
                DropDownStyle = ComboBoxStyle.DropDownList,
                Font = new Font("Segoe UI", 10)
            };
            cmbUrunler.SelectedIndexChanged += (sender, e) => MevcutStokGoster();
            checkPanel.Controls.Add(cmbUrunler);

            lblMevcutStok = new Label
            {
                Text = "Mevcut Stok: -",
                Left = 15,
                Top = 118,
                Width = 320,
                Height = 22,
                Font = new Font("Segoe UI", 10, FontStyle.Bold),
                ForeColor = Color.FromArgb(75, 85, 99)
            };
            checkPanel.Controls.Add(lblMevcutStok);

            checkPanel.Controls.Add(new Label { Text = "Istenen Miktar", Left = 15, Top = 145, Width = 320, Font = new Font("Segoe UI", 10, FontStyle.Bold) });
            txtMiktar = new TextBox { Left = 15, Top = 170, Width = 150, Font = new Font("Segoe UI", 10) };
            checkPanel.Controls.Add(txtMiktar);

            btnKontrolEt = new Button
            {
                Text = "Kontrol Et",
                Left = 180,
                Top = 166,
                Width = 155,
                Height = 34,
                BackColor = Color.FromArgb(59, 130, 246),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnKontrolEt.FlatAppearance.BorderSize = 0;
            btnKontrolEt.Click += BtnKontrolEt_Click;
            checkPanel.Controls.Add(btnKontrolEt);

            btnYenile = new Button
            {
                Text = "Listeyi Yenile",
                Left = 400,
                Top = 20,
                Width = 140,
                Height = 36,
                BackColor = Color.White,
                ForeColor = Color.FromArgb(75, 85, 99),
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnYenile.FlatAppearance.BorderColor = Color.FromArgb(209, 213, 219);
            btnYenile.Click += (sender, e) => StoklariListele();
            Controls.Add(btnYenile);

            dgvStok = new DataGridView
            {
                Left = 400,
                Top = 85,
                Width = 540,
                Height = 440,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false,
                BackgroundColor = Color.White,
                BorderStyle = BorderStyle.None,
                RowHeadersVisible = false
            };
            dgvStok.DataBindingComplete += DgvStok_DataBindingComplete;
            Controls.Add(dgvStok);

            Load += (sender, e) =>
            {
                UrunleriYukle();
                StoklariListele();
                NexusERP.Helpers.ThemeHelper.ApplyTheme(this);
            };
        }

        private void UrunleriYukle()
        {
            cmbUrunler.DataSource = UrunService.AktifUrunleriGetir();
            cmbUrunler.DisplayMember = "urunadi";
            cmbUrunler.ValueMember = "urunid";
        }

        private int MevcutStokGetir()
        {
            if (cmbUrunler.SelectedValue == null || cmbUrunler.SelectedValue is DataRowView)
                return 0;

            return UrunService.MevcutStokGetir(Convert.ToInt32(cmbUrunler.SelectedValue));
        }

        private void MevcutStokGoster()
        {
            if (cmbUrunler.SelectedValue == null || cmbUrunler.SelectedValue is DataRowView)
                return;

            lblMevcutStok.Text = "Mevcut Stok: " + MevcutStokGetir();
        }

        private void BtnKontrolEt_Click(object sender, EventArgs e)
        {
            if (!int.TryParse(txtMiktar.Text, out int istenenMiktar) || istenenMiktar <= 0)
            {
                MessageBox.Show("Istenen miktar 0'dan buyuk bir tam sayi olmalidir.", "Gecersiz Miktar", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            int mevcutStok = MevcutStokGetir();
            if (mevcutStok < istenenMiktar)
            {
                MessageBox.Show(
                    "Stok yetersiz! Mevcut stok (" + mevcutStok + ") talep edilen miktardan (" + istenenMiktar + ") daha az.",
                    "Yetersiz Stok",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Error);
            }
            else
            {
                MessageBox.Show(
                    "Stok yeterli! Isleme devam edebilirsiniz.",
                    "Stok Yeterli",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Information);
            }
        }

        private void StoklariListele()
        {
            dgvStok.DataSource = ApiClient.GetDataTable("/api/urunler/stok-takip");
        }

        private void DgvStok_DataBindingComplete(object sender, DataGridViewBindingCompleteEventArgs e)
        {
            foreach (DataGridViewRow row in dgvStok.Rows)
            {
                var durum = Convert.ToString(row.Cells["stokdurumu"].Value);
                if (durum == "Kritik")
                {
                    row.DefaultCellStyle.BackColor = Color.FromArgb(254, 226, 226); 
                    row.DefaultCellStyle.ForeColor = Color.FromArgb(153, 27, 27);
                }
                else if (durum == "Azaliyor")
                {
                    row.DefaultCellStyle.BackColor = Color.FromArgb(254, 243, 199); 
                    row.DefaultCellStyle.ForeColor = Color.FromArgb(146, 64, 14);
                }
                else
                {
                    row.DefaultCellStyle.BackColor = Color.FromArgb(240, 253, 244); 
                    row.DefaultCellStyle.ForeColor = Color.FromArgb(21, 128, 61);
                }
            }
        }
    }
}