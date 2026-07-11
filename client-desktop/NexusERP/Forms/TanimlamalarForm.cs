using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;
using NexusERP.Helpers;

namespace NexusERP
{
    public class TanimlamalarForm : Form
    {
        private TabControl tabControl;

        private int seciliMusteriID = 0;
        private TextBox txtMusteriAdi;
        private DataGridView dgvMusteriler;

        private int seciliMakineID = 0;
        private TextBox txtMakineAdi;
        private TextBox txtMakineKodu;
        private ComboBox cmbMakineDurum;
        private DataGridView dgvMakineler;

        private int seciliPersonelID = 0;
        private TextBox txtPersonelAdSoyad;
        private TextBox txtPersonelDepartman;
        private CheckBox chkPersonelAktif;
        private DataGridView dgvPersoneller;

        public TanimlamalarForm()
        {
            Text = "Sistem Tanımlamaları";
            Width = 980;
            Height = 620;
            StartPosition = FormStartPosition.CenterScreen;
            BackColor = Color.FromArgb(244, 247, 251);
            Font = new Font("Segoe UI", 10);

            var baslikLabel = new Label
            {
                Text = "Sistem Tanımlama Modülleri",
                Left = 30,
                Top = 15,
                Width = 500,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold),
                ForeColor = Color.FromArgb(17, 24, 39)
            };
            Controls.Add(baslikLabel);

            tabControl = new TabControl
            {
                Left = 30,
                Top = 65,
                Width = 905,
                Height = 490,
                Font = new Font("Segoe UI", 10, FontStyle.Bold),
                ItemSize = new Size(150, 30),
                SizeMode = TabSizeMode.Fixed
            };
            Controls.Add(tabControl);

            InitializeMusteriTab();
            InitializeMakineTab();
            InitializePersonelTab();

            Load += (sender, e) =>
            {
                MusterileriListele();
                MakineleriListele();
                PersonelleriListele();
                ThemeHelper.ApplyTheme(this);
            };
        }

        #region Musteri Tab
        private void InitializeMusteriTab()
        {
            var tab = new TabPage("Müşteri Yönetimi") { BackColor = Color.White };
            
            var layout = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                ColumnCount = 2,
                RowCount = 1,
                Padding = new Padding(15)
            };
            layout.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 320));
            layout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100));
            tab.Controls.Add(layout);

            var inputPanel = new Panel { Dock = DockStyle.Fill };
            inputPanel.Controls.Add(new Label { Text = "Müşteri Adı", Left = 10, Top = 20, Width = 120, Font = new Font("Segoe UI", 10, FontStyle.Bold) });
            txtMusteriAdi = new TextBox { Left = 10, Top = 45, Width = 280, Font = new Font("Segoe UI", 10) };
            inputPanel.Controls.Add(txtMusteriAdi);

            var btnPanel = new FlowLayoutPanel { Left = 10, Top = 95, Width = 290, Height = 100, FlowDirection = FlowDirection.LeftToRight };
            var btnKaydet = Buton("Ekle", Color.FromArgb(34, 197, 94), Color.White);
            var btnGuncelle = Buton("Güncelle", Color.FromArgb(59, 130, 246), Color.White);
            var btnSil = Buton("Sil", Color.FromArgb(239, 68, 68), Color.White);
            
            btnKaydet.Click += (s, e) => {
                if (string.IsNullOrWhiteSpace(txtMusteriAdi.Text)) { MessageBox.Show("Müşteri adı boş bırakılamaz."); return; }
                try
                {
                    ApiClient.Post("/api/musteriler", new { musteriadi = txtMusteriAdi.Text.Trim() });
                    txtMusteriAdi.Clear(); seciliMusteriID = 0; MusterileriListele();
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Müşteri kaydedilirken hata oluştu: " + ex.Message);
                }
            };

            btnGuncelle.Click += (s, e) => {
                if (seciliMusteriID == 0) { MessageBox.Show("Listeden bir müşteri seçin."); return; }
                if (string.IsNullOrWhiteSpace(txtMusteriAdi.Text)) { MessageBox.Show("Müşteri adı boş bırakılamaz."); return; }
                try
                {
                    ApiClient.Put($"/api/musteriler/{seciliMusteriID}", new { musteriadi = txtMusteriAdi.Text.Trim() });
                    txtMusteriAdi.Clear(); seciliMusteriID = 0; MusterileriListele();
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Müşteri güncellenirken hata oluştu: " + ex.Message);
                }
            };

            btnSil.Click += (s, e) => {
                if (seciliMusteriID == 0) { MessageBox.Show("Listeden bir müşteri seçin."); return; }
                var confirm = MessageBox.Show("Müşteriyi silmek istediğinize emin misiniz?", "Silme Onayı", MessageBoxButtons.YesNo);
                if (confirm == DialogResult.Yes) {
                    try {
                        ApiClient.Delete($"/api/musteriler/{seciliMusteriID}");
                        txtMusteriAdi.Clear(); seciliMusteriID = 0; MusterileriListele();
                    } catch {
                        MessageBox.Show("Bu müşteriye ait aktif siparişler olduğu için silinemez.");
                    }
                }
            };

            btnPanel.Controls.Add(btnKaydet);
            btnPanel.Controls.Add(btnGuncelle);
            btnPanel.Controls.Add(btnSil);
            inputPanel.Controls.Add(btnPanel);

            dgvMusteriler = new DataGridView
            {
                Dock = DockStyle.Fill,
                ReadOnly = true,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false,
                RowHeadersVisible = false,
                BackgroundColor = Color.FromArgb(240, 243, 248),
                BorderStyle = BorderStyle.None,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill
            };
            dgvMusteriler.CellClick += (s, e) => {
                if (e.RowIndex >= 0 && !dgvMusteriler.Rows[e.RowIndex].IsNewRow) {
                    var row = dgvMusteriler.Rows[e.RowIndex];
                    seciliMusteriID = Convert.ToInt32(row.Cells["musteriid"].Value);
                    txtMusteriAdi.Text = Convert.ToString(row.Cells["musteriadi"].Value);
                }
            };

            layout.Controls.Add(inputPanel, 0, 0);
            layout.Controls.Add(dgvMusteriler, 1, 0);
            tabControl.TabPages.Add(tab);
        }

        private void MusterileriListele()
        {
            try
            {
                dgvMusteriler.DataSource = ApiClient.GetDataTable("/api/musteriler");
                if (dgvMusteriler.Columns["musteriid"] != null)
                    dgvMusteriler.Columns["musteriid"].HeaderText = "Müşteri ID";
                if (dgvMusteriler.Columns["musteriadi"] != null)
                    dgvMusteriler.Columns["musteriadi"].HeaderText = "Müşteri Ünvanı / Adı";
            }
            catch (Exception ex)
            {
                MessageBox.Show("Müşteriler listelenirken hata oluştu: " + ex.Message);
            }
        }
        #endregion

        #region Makine Tab
        private void InitializeMakineTab()
        {
            var tab = new TabPage("Makine Yönetimi") { BackColor = Color.White };
            
            var layout = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                ColumnCount = 2,
                RowCount = 1,
                Padding = new Padding(15)
            };
            layout.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 320));
            layout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100));
            tab.Controls.Add(layout);

            var inputPanel = new Panel { Dock = DockStyle.Fill };
            inputPanel.Controls.Add(new Label { Text = "Makine Adı", Left = 10, Top = 10, Width = 120, Font = new Font("Segoe UI", 10, FontStyle.Bold) });
            txtMakineAdi = new TextBox { Left = 10, Top = 35, Width = 280, Font = new Font("Segoe UI", 10) };
            inputPanel.Controls.Add(txtMakineAdi);

            inputPanel.Controls.Add(new Label { Text = "Makine Kodu", Left = 10, Top = 75, Width = 120, Font = new Font("Segoe UI", 10, FontStyle.Bold) });
            txtMakineKodu = new TextBox { Left = 10, Top = 100, Width = 280, Font = new Font("Segoe UI", 10) };
            inputPanel.Controls.Add(txtMakineKodu);

            inputPanel.Controls.Add(new Label { Text = "Makine Durumu", Left = 10, Top = 140, Width = 120, Font = new Font("Segoe UI", 10, FontStyle.Bold) });
            cmbMakineDurum = new ComboBox { Left = 10, Top = 165, Width = 280, DropDownStyle = ComboBoxStyle.DropDownList, Font = new Font("Segoe UI", 10) };
            cmbMakineDurum.Items.AddRange(new object[] { "Aktif", "Pasif" });
            cmbMakineDurum.SelectedIndex = 0;
            inputPanel.Controls.Add(cmbMakineDurum);

            var btnPanel = new FlowLayoutPanel { Left = 10, Top = 215, Width = 290, Height = 100, FlowDirection = FlowDirection.LeftToRight };
            var btnKaydet = Buton("Ekle", Color.FromArgb(34, 197, 94), Color.White);
            var btnGuncelle = Buton("Güncelle", Color.FromArgb(59, 130, 246), Color.White);
            var btnSil = Buton("Sil", Color.FromArgb(239, 68, 68), Color.White);
            
            btnKaydet.Click += (s, e) => {
                if (string.IsNullOrWhiteSpace(txtMakineAdi.Text) || string.IsNullOrWhiteSpace(txtMakineKodu.Text)) { MessageBox.Show("Alanlar boş bırakılamaz."); return; }
                try
                {
                    var data = new { makineadi = txtMakineAdi.Text.Trim(), makinekodu = txtMakineKodu.Text.Trim(), durum = cmbMakineDurum.SelectedItem.ToString() };
                    ApiClient.Post("/api/makineler", data);
                    ClearMakine(); MakineleriListele();
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Makine kaydedilirken hata oluştu: " + ex.Message);
                }
            };

            btnGuncelle.Click += (s, e) => {
                if (seciliMakineID == 0) { MessageBox.Show("Listeden bir makine seçin."); return; }
                if (string.IsNullOrWhiteSpace(txtMakineAdi.Text) || string.IsNullOrWhiteSpace(txtMakineKodu.Text)) { MessageBox.Show("Alanlar boş bırakılamaz."); return; }
                try
                {
                    var data = new { makineadi = txtMakineAdi.Text.Trim(), makinekodu = txtMakineKodu.Text.Trim(), durum = cmbMakineDurum.SelectedItem.ToString() };
                    ApiClient.Put($"/api/makineler/{seciliMakineID}", data);
                    ClearMakine(); MakineleriListele();
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Makine güncellenirken hata oluştu: " + ex.Message);
                }
            };

            btnSil.Click += (s, e) => {
                if (seciliMakineID == 0) { MessageBox.Show("Listeden bir makine seçin."); return; }
                var confirm = MessageBox.Show("Makineyi silmek istediğinize emin misiniz?", "Silme Onayı", MessageBoxButtons.YesNo);
                if (confirm == DialogResult.Yes) {
                    try {
                        ApiClient.Delete($"/api/makineler/{seciliMakineID}");
                        ClearMakine(); MakineleriListele();
                    } catch {
                        MessageBox.Show("Bu makineye ait üretim kayıtları bulunduğu için silinemez.");
                    }
                }
            };

            btnPanel.Controls.Add(btnKaydet);
            btnPanel.Controls.Add(btnGuncelle);
            btnPanel.Controls.Add(btnSil);
            inputPanel.Controls.Add(btnPanel);

            dgvMakineler = new DataGridView
            {
                Dock = DockStyle.Fill,
                ReadOnly = true,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false,
                RowHeadersVisible = false,
                BackgroundColor = Color.FromArgb(240, 243, 248),
                BorderStyle = BorderStyle.None,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill
            };
            dgvMakineler.CellClick += (s, e) => {
                if (e.RowIndex >= 0 && !dgvMakineler.Rows[e.RowIndex].IsNewRow) {
                    var row = dgvMakineler.Rows[e.RowIndex];
                    seciliMakineID = Convert.ToInt32(row.Cells["makineid"].Value);
                    txtMakineAdi.Text = Convert.ToString(row.Cells["makineadi"].Value);
                    txtMakineKodu.Text = Convert.ToString(row.Cells["makinekodu"].Value);
                    cmbMakineDurum.SelectedItem = Convert.ToString(row.Cells["durum"].Value);
                }
            };

            layout.Controls.Add(inputPanel, 0, 0);
            layout.Controls.Add(dgvMakineler, 1, 0);
            tabControl.TabPages.Add(tab);
        }

        private void ClearMakine()
        {
            txtMakineAdi.Clear();
            txtMakineKodu.Clear();
            cmbMakineDurum.SelectedIndex = 0;
            seciliMakineID = 0;
        }

        private void MakineleriListele()
        {
            try
            {
                dgvMakineler.DataSource = ApiClient.GetDataTable("/api/makineler");
                if (dgvMakineler.Columns["makineid"] != null)
                    dgvMakineler.Columns["makineid"].HeaderText = "Makine ID";
                if (dgvMakineler.Columns["makineadi"] != null)
                    dgvMakineler.Columns["makineadi"].HeaderText = "Makine Adı";
                if (dgvMakineler.Columns["makinekodu"] != null)
                    dgvMakineler.Columns["makinekodu"].HeaderText = "Makine Kodu";
                if (dgvMakineler.Columns["durum"] != null)
                    dgvMakineler.Columns["durum"].HeaderText = "Durum";
            }
            catch (Exception ex)
            {
                MessageBox.Show("Makineler listelenirken hata oluştu: " + ex.Message);
            }
        }
        #endregion

        #region Personel Tab
        private void InitializePersonelTab()
        {
            var tab = new TabPage("Personel Yönetimi") { BackColor = Color.White };
            
            var layout = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                ColumnCount = 2,
                RowCount = 1,
                Padding = new Padding(15)
            };
            layout.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 320));
            layout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100));
            tab.Controls.Add(layout);

            var inputPanel = new Panel { Dock = DockStyle.Fill };
            inputPanel.Controls.Add(new Label { Text = "Personel Ad Soyad", Left = 10, Top = 10, Width = 150, Font = new Font("Segoe UI", 10, FontStyle.Bold) });
            txtPersonelAdSoyad = new TextBox { Left = 10, Top = 35, Width = 280, Font = new Font("Segoe UI", 10) };
            inputPanel.Controls.Add(txtPersonelAdSoyad);

            inputPanel.Controls.Add(new Label { Text = "Departman / Ünvan", Left = 10, Top = 75, Width = 150, Font = new Font("Segoe UI", 10, FontStyle.Bold) });
            txtPersonelDepartman = new TextBox { Left = 10, Top = 100, Width = 280, Font = new Font("Segoe UI", 10) };
            inputPanel.Controls.Add(txtPersonelDepartman);

            chkPersonelAktif = new CheckBox { Text = "Personel Aktif Mi?", Left = 10, Top = 145, Width = 200, Checked = true, Font = new Font("Segoe UI", 10, FontStyle.Bold) };
            inputPanel.Controls.Add(chkPersonelAktif);

            var btnPanel = new FlowLayoutPanel { Left = 10, Top = 190, Width = 290, Height = 100, FlowDirection = FlowDirection.LeftToRight };
            var btnKaydet = Buton("Ekle", Color.FromArgb(34, 197, 94), Color.White);
            var btnGuncelle = Buton("Güncelle", Color.FromArgb(59, 130, 246), Color.White);
            var btnSil = Buton("Sil", Color.FromArgb(239, 68, 68), Color.White);
            
            btnKaydet.Click += (s, e) => {
                if (string.IsNullOrWhiteSpace(txtPersonelAdSoyad.Text)) { MessageBox.Show("İsim soyisim alanı boş bırakılamaz."); return; }
                try
                {
                    var data = new { adsoyad = txtPersonelAdSoyad.Text.Trim(), departman = txtPersonelDepartman.Text.Trim(), aktifmi = chkPersonelAktif.Checked };
                    ApiClient.Post("/api/personeller", data);
                    ClearPersonel(); PersonelleriListele();
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Personel kaydedilirken hata oluştu: " + ex.Message);
                }
            };

            btnGuncelle.Click += (s, e) => {
                if (seciliPersonelID == 0) { MessageBox.Show("Listeden bir personel seçin."); return; }
                if (string.IsNullOrWhiteSpace(txtPersonelAdSoyad.Text)) { MessageBox.Show("İsim soyisim alanı boş bırakılamaz."); return; }
                try
                {
                    var data = new { adsoyad = txtPersonelAdSoyad.Text.Trim(), departman = txtPersonelDepartman.Text.Trim(), aktifmi = chkPersonelAktif.Checked };
                    ApiClient.Put($"/api/personeller/{seciliPersonelID}", data);
                    ClearPersonel(); PersonelleriListele();
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Personel güncellenirken hata oluştu: " + ex.Message);
                }
            };

            btnSil.Click += (s, e) => {
                if (seciliPersonelID == 0) { MessageBox.Show("Listeden bir personel seçin."); return; }
                var confirm = MessageBox.Show("Personeli silmek istediğinize emin misiniz?", "Silme Onayı", MessageBoxButtons.YesNo);
                if (confirm == DialogResult.Yes) {
                    try {
                        ApiClient.Delete($"/api/personeller/{seciliPersonelID}");
                        ClearPersonel(); PersonelleriListele();
                    } catch {
                        MessageBox.Show("Bu personele ait aktif üretim kayıtları bulunduğu için silinemez.");
                    }
                }
            };

            btnPanel.Controls.Add(btnKaydet);
            btnPanel.Controls.Add(btnGuncelle);
            btnPanel.Controls.Add(btnSil);
            inputPanel.Controls.Add(btnPanel);

            dgvPersoneller = new DataGridView
            {
                Dock = DockStyle.Fill,
                ReadOnly = true,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false,
                RowHeadersVisible = false,
                BackgroundColor = Color.FromArgb(240, 243, 248),
                BorderStyle = BorderStyle.None,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill
            };
            dgvPersoneller.CellClick += (s, e) => {
                if (e.RowIndex >= 0 && !dgvPersoneller.Rows[e.RowIndex].IsNewRow) {
                    var row = dgvPersoneller.Rows[e.RowIndex];
                    seciliPersonelID = Convert.ToInt32(row.Cells["personelid"].Value);
                    txtPersonelAdSoyad.Text = Convert.ToString(row.Cells["adsoyad"].Value);
                    txtPersonelDepartman.Text = Convert.ToString(row.Cells["departman"].Value);
                    chkPersonelAktif.Checked = Convert.ToBoolean(row.Cells["aktifmi"].Value);
                }
            };

            layout.Controls.Add(inputPanel, 0, 0);
            layout.Controls.Add(dgvPersoneller, 1, 0);
            tabControl.TabPages.Add(tab);
        }

        private void ClearPersonel()
        {
            txtPersonelAdSoyad.Clear();
            txtPersonelDepartman.Clear();
            chkPersonelAktif.Checked = true;
            seciliPersonelID = 0;
        }

        private void PersonelleriListele()
        {
            try
            {
                dgvPersoneller.DataSource = ApiClient.GetDataTable("/api/personeller");
                if (dgvPersoneller.Columns["personelid"] != null)
                    dgvPersoneller.Columns["personelid"].HeaderText = "Personel ID";
                if (dgvPersoneller.Columns["adsoyad"] != null)
                    dgvPersoneller.Columns["adsoyad"].HeaderText = "Adı Soyadı";
                if (dgvPersoneller.Columns["departman"] != null)
                    dgvPersoneller.Columns["departman"].HeaderText = "Departmanı";
                if (dgvPersoneller.Columns["aktifmi"] != null)
                    dgvPersoneller.Columns["aktifmi"].HeaderText = "Aktif Mi?";
            }
            catch (Exception ex)
            {
                MessageBox.Show("Personeller listelenirken hata oluştu: " + ex.Message);
            }
        }
        #endregion

        private Button Buton(string text, Color backColor, Color foreColor)
        {
            return new Button
            {
                Text = text,
                Width = 85,
                Height = 36,
                BackColor = backColor,
                ForeColor = foreColor,
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 9, FontStyle.Bold),
                Margin = new Padding(0, 0, 8, 8)
            };
        }
    }
}