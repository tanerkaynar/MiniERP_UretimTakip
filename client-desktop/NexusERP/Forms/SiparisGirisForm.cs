using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;

namespace NexusERP
{
    public class SiparisGirisForm : Form
    {
        private readonly ComboBox cmbMusteriler;
        private readonly ComboBox cmbUrunler;
        private readonly TextBox txtMiktar;
        private readonly TextBox txtBirimFiyat;
        private readonly Button btnKaydet;
        private readonly DataGridView dgvSiparisler;

        public SiparisGirisForm()
        {
            Text = "Siparis Giris Formu";
            Width = 1000;
            Height = 620;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label { Text = "Siparis Girisi", Left = 30, Top = 20, Width = 300, Height = 35, Font = new Font("Segoe UI", 16, FontStyle.Bold) });

            Controls.Add(Etiket("Musteri", 30, 80));
            cmbMusteriler = Combo(150, 76);
            Controls.Add(cmbMusteriler);

            Controls.Add(Etiket("Urun", 30, 125));
            cmbUrunler = Combo(150, 121);
            Controls.Add(cmbUrunler);

            Controls.Add(Etiket("Miktar", 30, 170));
            txtMiktar = MetinKutusu(150, 166);
            Controls.Add(txtMiktar);

            Controls.Add(Etiket("Birim Fiyat", 30, 215));
            txtBirimFiyat = MetinKutusu(150, 211);
            Controls.Add(txtBirimFiyat);

            btnKaydet = new Button { Text = "Siparis Kaydet", Left = 150, Top = 260, Width = 160, Height = 42, Font = new Font("Segoe UI", 10, FontStyle.Bold) };
            btnKaydet.Click += BtnKaydet_Click;
            Controls.Add(btnKaydet);

            dgvSiparisler = new DataGridView { Left = 30, Top = 330, Width = 900, Height = 210, ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            Controls.Add(dgvSiparisler);

            Load += (sender, e) =>
            {
                MusterileriYukle();
                UrunleriYukle();
                SiparisleriListele();
                NexusERP.Helpers.ThemeHelper.ApplyTheme(this);
            };
        }

        private void BtnKaydet_Click(object sender, EventArgs e)
        {
            if (cmbMusteriler.SelectedValue == null || cmbUrunler.SelectedValue == null)
            {
                MessageBox.Show("Musteri ve Urun secimi zorunludur.");
                return;
            }

            if (!int.TryParse(txtMiktar.Text, out int miktar) || miktar <= 0)
            {
                MessageBox.Show("Miktar 0'dan buyuk sayi olmalidir.");
                return;
            }

            if (!decimal.TryParse(txtBirimFiyat.Text, out decimal birimFiyat) || birimFiyat < 0)
            {
                MessageBox.Show("Birim fiyat 0 veya daha buyuk sayi olmalidir.");
                return;
            }

            try
            {
                int musteriId = Convert.ToInt32(cmbMusteriler.SelectedValue);
                int urunId = Convert.ToInt32(cmbUrunler.SelectedValue);
                NexusERP.Services.SiparisService.SiparisKaydet(musteriId, urunId, miktar, birimFiyat);
                MessageBox.Show("Siparis kaydedildi.");
                txtMiktar.Clear();
                txtBirimFiyat.Clear();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Siparis kaydedilirken hata olustu: " + ex.Message);
            }

            SiparisleriListele();
        }

        private void MusterileriYukle()
        {
            cmbMusteriler.DataSource = NexusERP.Services.SiparisService.MusterileriGetir();
            cmbMusteriler.DisplayMember = "musteriadi";
            cmbMusteriler.ValueMember = "musteriid";
        }

        private void UrunleriYukle()
        {
            cmbUrunler.DataSource = NexusERP.Services.UrunService.AktifUrunleriGetir();
            cmbUrunler.DisplayMember = "urunadi";
            cmbUrunler.ValueMember = "urunid";
        }

        private void SiparisleriListele()
        {
            dgvSiparisler.DataSource = NexusERP.Services.SiparisService.SiparisleriGetir();
        }

        private Label Etiket(string text, int left, int top) => new Label { Text = text, Left = left, Top = top, Width = 110, Height = 28, Font = new Font("Segoe UI", 10) };
        private ComboBox Combo(int left, int top) => new ComboBox { Left = left, Top = top, Width = 280, DropDownStyle = ComboBoxStyle.DropDownList, Font = new Font("Segoe UI", 10) };
        private TextBox MetinKutusu(int left, int top) => new TextBox { Left = left, Top = top, Width = 160, Font = new Font("Segoe UI", 10) };
    }
}