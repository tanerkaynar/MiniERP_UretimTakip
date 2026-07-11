using System;
using System.Drawing;
using System.Windows.Forms;

namespace NexusERP
{
    public class UretimGirisForm : Form
    {
        private readonly ComboBox cmbUrunler;
        private readonly ComboBox cmbMakineler;
        private readonly ComboBox cmbPersoneller;
        private readonly TextBox txtUretimAdedi;
        private readonly TextBox txtAciklama;
        private readonly Button btnKaydet;
        private readonly Button btnSil;
        private readonly DataGridView dgvUretimKayitlari;

        public UretimGirisForm()
        {
            Text = "Uretim Giris Formu";
            Width = 1050;
            Height = 650;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label
            {
                Text = "Uretim Girisi",
                Left = 30,
                Top = 20,
                Width = 300,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            });

            Controls.Add(Etiket("Urun", 30, 80));
            cmbUrunler = Combo(160, 76);
            Controls.Add(cmbUrunler);

            Controls.Add(Etiket("Makine", 30, 125));
            cmbMakineler = Combo(160, 121);
            Controls.Add(cmbMakineler);

            Controls.Add(Etiket("Personel", 30, 170));
            cmbPersoneller = Combo(160, 166);
            Controls.Add(cmbPersoneller);

            Controls.Add(Etiket("Uretim Adedi", 30, 215));
            txtUretimAdedi = MetinKutusu(160, 211);
            Controls.Add(txtUretimAdedi);

            Controls.Add(Etiket("Aciklama", 30, 260));
            txtAciklama = MetinKutusu(160, 256);
            Controls.Add(txtAciklama);

            btnKaydet = new Button
            {
                Text = "Uretim Kaydi Olustur",
                Left = 160,
                Top = 310,
                Width = 210,
                Height = 42,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnKaydet.Click += BtnKaydet_Click;
            Controls.Add(btnKaydet);

            btnSil = new Button
            {
                Text = "Seçilen Üretim Kaydını Sil",
                Left = 385,
                Top = 310,
                Width = 220,
                Height = 42,
                Font = new Font("Segoe UI", 10, FontStyle.Bold),
                BackColor = Color.FromArgb(239, 68, 68),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat
            };
            btnSil.FlatAppearance.BorderSize = 0;
            btnSil.Click += BtnSil_Click;
            Controls.Add(btnSil);

            dgvUretimKayitlari = new DataGridView
            {
                Left = 30,
                Top = 380,
                Width = 960,
                Height = 200,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false
            };
            Controls.Add(dgvUretimKayitlari);

            Load += (sender, e) =>
            {
                UrunleriYukle();
                MakineleriYukle();
                PersonelleriYukle();

                if (NexusERP.Helpers.SessionHelper.Rol != null && 
                    NexusERP.Helpers.SessionHelper.Rol.Equals("Operator", StringComparison.OrdinalIgnoreCase))
                {
                    if (NexusERP.Helpers.SessionHelper.PersonelID != null)
                    {
                        cmbPersoneller.SelectedValue = NexusERP.Helpers.SessionHelper.PersonelID.Value;
                        cmbPersoneller.Enabled = false;
                    }
                }

                UretimKayitlariniListele();
                NexusERP.Helpers.ThemeHelper.ApplyTheme(this);
            };
        }

        private void BtnKaydet_Click(object sender, EventArgs e)
        {
            if (cmbUrunler.SelectedValue == null || cmbMakineler.SelectedValue == null || cmbPersoneller.SelectedValue == null)
            {
                MessageBox.Show("Urun, makine ve personel secimi zorunludur.");
                return;
            }

            if (!int.TryParse(txtUretimAdedi.Text, out int uretimAdedi) || uretimAdedi <= 0)
            {
                MessageBox.Show("Uretim adedi 0'dan buyuk bir sayi olmalidir.");
                return;
            }

            try
            {
                int urunId = Convert.ToInt32(cmbUrunler.SelectedValue);
                int makineId = Convert.ToInt32(cmbMakineler.SelectedValue);
                int personelId = Convert.ToInt32(cmbPersoneller.SelectedValue);

                NexusERP.Services.UretimService.UretimKaydet(urunId, makineId, personelId, uretimAdedi, txtAciklama.Text.Trim());

                MessageBox.Show("Uretim kaydi olusturuldu ve stok miktari guncellendi.");
                txtUretimAdedi.Clear();
                txtAciklama.Clear();
                UretimKayitlariniListele();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Uretim kaydi sirasinda hata olustu: " + ex.Message);
            }
        }

        private void UrunleriYukle()
        {
            cmbUrunler.DataSource = NexusERP.Services.UrunService.AktifUrunleriGetir();
            cmbUrunler.DisplayMember = "urunadi";
            cmbUrunler.ValueMember = "urunid";
        }

        private void MakineleriYukle()
        {
            cmbMakineler.DataSource = NexusERP.Services.UretimService.AktifMakineleriGetir();
            cmbMakineler.DisplayMember = "makineadi";
            cmbMakineler.ValueMember = "makineid";
        }

        private void PersonelleriYukle()
        {
            cmbPersoneller.DataSource = NexusERP.Services.UretimService.AktifPersonelleriGetir();
            cmbPersoneller.DisplayMember = "adsoyad";
            cmbPersoneller.ValueMember = "personelid";
        }

        private void UretimKayitlariniListele()
        {
            dgvUretimKayitlari.DataSource = NexusERP.Services.UretimService.TumUretimKayitlariniGetir();
        }

        private void BtnSil_Click(object sender, EventArgs e)
        {
            if (dgvUretimKayitlari.CurrentRow == null || dgvUretimKayitlari.CurrentRow.Index < 0)
            {
                MessageBox.Show("Lütfen silmek istediğiniz üretim kaydını seçin.", "Hata", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            var confirm = MessageBox.Show("Seçilen üretim kaydını silmek istediğinize emin misiniz? Bu işlem stokları geri güncelleyecektir.", "Silme Onayı", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            if (confirm == DialogResult.Yes)
            {
                try
                {
                    int uretimId = Convert.ToInt32(dgvUretimKayitlari.CurrentRow.Cells["uretimid"].Value);
                    NexusERP.Services.UretimService.UretimSil(uretimId);
                    MessageBox.Show("Üretim kaydı silindi ve stok miktarı geri çekildi.", "Başarılı", MessageBoxButtons.OK, MessageBoxIcon.Information);
                    UretimKayitlariniListele();
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Üretim kaydı silinirken hata oluştu: " + ex.Message, "Hata", MessageBoxButtons.OK, MessageBoxIcon.Error);
                }
            }
        }

        private Label Etiket(string text, int left, int top)
        {
            return new Label { Text = text, Left = left, Top = top, Width = 120, Height = 28, Font = new Font("Segoe UI", 10) };
        }

        private ComboBox Combo(int left, int top)
        {
            return new ComboBox { Left = left, Top = top, Width = 280, Height = 30, Font = new Font("Segoe UI", 10), DropDownStyle = ComboBoxStyle.DropDownList };
        }

        private TextBox MetinKutusu(int left, int top)
        {
            return new TextBox { Left = left, Top = top, Width = 280, Height = 28, Font = new Font("Segoe UI", 10) };
        }
    }
}