using System;
using System.Drawing;
using System.Windows.Forms;
using NexusERP.Services;
using NexusERP.Helpers;

namespace NexusERP
{
    public class RegisterForm : Form
    {
        private readonly TextBox txtKullaniciAdi;
        private readonly TextBox txtParola;
        private readonly ComboBox cmbRol;
        private readonly ComboBox cmbPersonel;
        private readonly Button btnKaydet;
        private readonly Button btnIptal;

        public RegisterForm()
        {
            Text = "Yeni Kullanici Kaydi";
            Width = 460;
            Height = 450;
            StartPosition = FormStartPosition.CenterScreen;
            FormBorderStyle = FormBorderStyle.FixedDialog;
            MaximizeBox = false;
            MinimizeBox = false;
            BackColor = Color.FromArgb(245, 247, 250);
            Font = new Font("Segoe UI", 10);

            var ustPanel = new Panel
            {
                Dock = DockStyle.Top,
                Height = 86,
                BackColor = Color.FromArgb(25, 39, 58)
            };
            Controls.Add(ustPanel);

            ustPanel.Controls.Add(new Label
            {
                Text = "Yeni Hesap Olustur",
                Left = 32,
                Top = 18,
                Width = 340,
                Height = 30,
                ForeColor = Color.White,
                Font = new Font("Segoe UI", 17, FontStyle.Bold)
            });

            ustPanel.Controls.Add(new Label
            {
                Text = "Sisteme kaydolmak icin asagidaki alanlari doldurun",
                Left = 34,
                Top = 51,
                Width = 340,
                Height = 22,
                ForeColor = Color.FromArgb(200, 208, 218),
                Font = new Font("Segoe UI", 9)
            });

            Controls.Add(Etiket("Kullanici Adi", 44, 118));
            txtKullaniciAdi = MetinKutusu(174, 114);
            Controls.Add(txtKullaniciAdi);

            Controls.Add(Etiket("Parola", 44, 166));
            txtParola = MetinKutusu(174, 162);
            txtParola.UseSystemPasswordChar = true;
            Controls.Add(txtParola);

            Controls.Add(Etiket("Yetki / Rol", 44, 214));
            cmbRol = new ComboBox
            {
                Left = 174,
                Top = 210,
                Width = 230,
                Height = 28,
                DropDownStyle = ComboBoxStyle.DropDownList,
                Font = new Font("Segoe UI", 10)
            };
            cmbRol.Items.Add("Operator");
            cmbRol.Items.Add("Sevkiyatci");
            cmbRol.Items.Add("Planlamaci");
            cmbRol.SelectedIndex = 0;
            cmbRol.SelectedIndexChanged += (s, e) => cmbPersonel.SelectedIndex = -1;
            Controls.Add(cmbRol);

            Controls.Add(Etiket("Personel Seçin", 44, 262));
            cmbPersonel = new ComboBox
            {
                Left = 174,
                Top = 258,
                Width = 230,
                Height = 28,
                DropDownStyle = ComboBoxStyle.DropDownList,
                Font = new Font("Segoe UI", 10)
            };
            Controls.Add(cmbPersonel);

            btnKaydet = new Button
            {
                Text = "Kaydol",
                Left = 174,
                Top = 320,
                Width = 140,
                Height = 40,
                BackColor = Color.FromArgb(34, 197, 94),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnKaydet.FlatAppearance.BorderSize = 0;
            btnKaydet.Click += BtnKaydet_Click;
            Controls.Add(btnKaydet);

            btnIptal = new Button
            {
                Text = "Iptal",
                Left = 322,
                Top = 320,
                Width = 82,
                Height = 40,
                BackColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 10)
            };
            btnIptal.FlatAppearance.BorderColor = Color.FromArgb(205, 213, 224);
            btnIptal.Click += (sender, e) => Close();
            Controls.Add(btnIptal);

            AcceptButton = btnKaydet;
            CancelButton = btnIptal;

            Load += (sender, e) =>
            {
                PersonelleriYukle();
            };
        }

        private void PersonelleriYukle()
        {
            try
            {
                var dt = ApiClient.GetDataTable("/api/personeller/kullanici-atanmamis");
                cmbPersonel.DataSource = dt;
                cmbPersonel.DisplayMember = "adsoyad";
                cmbPersonel.ValueMember = "personelid";
                cmbPersonel.SelectedIndex = -1;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Boştaki personeller yüklenirken hata oluştu: " + ex.Message, "Hata", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void BtnKaydet_Click(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(txtKullaniciAdi.Text) || string.IsNullOrWhiteSpace(txtParola.Text))
            {
                MessageBox.Show("Kullanici adi ve parola bos olamaz.", "Eksik Bilgi", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            if (cmbPersonel.SelectedValue == null)
            {
                MessageBox.Show("Lütfen bir personel seçin. Tüm roller için personel eşleşmesi zorunludur.", "Eksik Bilgi", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            try
            {
                string rol = cmbRol.SelectedItem.ToString();
                int personelId = Convert.ToInt32(cmbPersonel.SelectedValue);
                bool sonuc = KullaniciService.KullaniciKaydet(txtKullaniciAdi.Text, txtParola.Text, rol, personelId);

                if (sonuc)
                {
                    MessageBox.Show("Hesabiniz basariyla olusturuldu. Simdi giris yapabilirsiniz.", "Kayit Basarili", MessageBoxButtons.OK, MessageBoxIcon.Information);
                    Close();
                }
                else
                {
                    MessageBox.Show("Kayit olusturulamadi. Lutfen bilgilerinizi kontrol edin.", "Kayit Basarisiz", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Kayit sirasinda hata olustu:" + Environment.NewLine + ex.Message, "Hata", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private Label Etiket(string text, int left, int top)
        {
            return new Label
            {
                Text = text,
                Left = left,
                Top = top,
                Width = 120,
                Height = 28,
                ForeColor = Color.FromArgb(51, 65, 85),
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
        }

        private TextBox MetinKutusu(int left, int top)
        {
            return new TextBox
            {
                Left = left,
                Top = top,
                Width = 230,
                Height = 28,
                Font = new Font("Segoe UI", 10)
            };
        }
    }
}