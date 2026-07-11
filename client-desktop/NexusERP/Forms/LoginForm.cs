using System;
using System.Drawing;
using System.Windows.Forms;
using NexusERP.Helpers;

namespace NexusERP
{
    public class LoginForm : Form
    {
        private readonly TextBox txtKullaniciAdi;
        private readonly TextBox txtParola;
        private readonly Button btnGiris;
        private readonly Button btnIptal;

        public string KullaniciAdi { get; private set; }

        public LoginForm()
        {
            Text = "Kullanici Girisi";
            Width = 460;
            Height = 340;
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
                Text = "Mini ERP",
                Left = 32,
                Top = 18,
                Width = 340,
                Height = 30,
                ForeColor = Color.White,
                Font = new Font("Segoe UI", 17, FontStyle.Bold)
            });

            ustPanel.Controls.Add(new Label
            {
                Text = "Uretim takip sistemine giris",
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

            btnGiris = new Button
            {
                Text = "Giris Yap",
                Left = 174,
                Top = 220,
                Width = 140,
                Height = 40,
                BackColor = Color.FromArgb(35, 99, 180),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnGiris.FlatAppearance.BorderSize = 0;
            btnGiris.Click += BtnGiris_Click;
            Controls.Add(btnGiris);

            btnIptal = new Button
            {
                Text = "Iptal",
                Left = 322,
                Top = 220,
                Width = 82,
                Height = 40,
                BackColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 10)
            };
            btnIptal.FlatAppearance.BorderColor = Color.FromArgb(205, 213, 224);
            btnIptal.Click += (sender, e) => Close();
            Controls.Add(btnIptal);

            var lnkKayitOl = new LinkLabel
            {
                Text = "Yeni Hesap Olustur (Kayit Ol)",
                Left = 174,
                Top = 275,
                Width = 230,
                Height = 22,
                Font = new Font("Segoe UI", 9),
                LinkColor = Color.FromArgb(35, 99, 180)
            };
            lnkKayitOl.LinkClicked += (sender, e) =>
            {
                using (var regForm = new RegisterForm())
                {
                    regForm.ShowDialog();
                }
            };
            Controls.Add(lnkKayitOl);

            AcceptButton = btnGiris;
            CancelButton = btnIptal;

            Load += (sender, e) => NexusERP.Helpers.ThemeHelper.ApplyTheme(this);
        }

        public string KullaniciRol { get; private set; }

        private void BtnGiris_Click(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(txtKullaniciAdi.Text) || string.IsNullOrWhiteSpace(txtParola.Text))
            {
                MessageBox.Show("Kullanici adi ve parola bos olamaz.", "Eksik Bilgi", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            try
            {
                var user = NexusERP.Services.KullaniciService.Dogrula(txtKullaniciAdi.Text, txtParola.Text);
                if (user != null)
                {
                    KullaniciAdi = user.KullaniciAdi;
                    KullaniciRol = user.Rol;

                    SessionHelper.KullaniciAdi = user.KullaniciAdi;
                    SessionHelper.Rol = user.Rol;
                    if (user.Personel != null)
                    {
                        SessionHelper.PersonelID = user.Personel.PersonelID;
                        SessionHelper.PersonelAdSoyad = user.Personel.AdSoyad;
                    }
                    else
                    {
                        SessionHelper.PersonelID = null;
                        SessionHelper.PersonelAdSoyad = null;
                    }

                    DialogResult = DialogResult.OK;
                    Close();
                    return;
                }

                MessageBox.Show("Kullanici adi veya parola hatali.", "Giris Basarisiz", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            }
            catch (Exception ex)
            {
                MessageBox.Show("Giris sirasinda hata olustu:" + Environment.NewLine + ex.Message, "Giris Hatasi", MessageBoxButtons.OK, MessageBoxIcon.Error);
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