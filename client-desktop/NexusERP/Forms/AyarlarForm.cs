using System;
using System.Drawing;
using System.Windows.Forms;
using NexusERP.Helpers;

namespace NexusERP
{
    public class AyarlarForm : Form
    {
        private readonly RadioButton rdbLight;
        private readonly RadioButton rdbDark;
        private readonly TextBox txtSunucu;
        private readonly Button btnKaydet;
        private readonly Button btnIptal;

        public AyarlarForm()
        {
            Text = "Sistem Ayarları";
            Width = 480;
            Height = 360;
            StartPosition = FormStartPosition.CenterScreen;
            FormBorderStyle = FormBorderStyle.FixedDialog;
            MaximizeBox = false;
            MinimizeBox = false;
            BackColor = Color.FromArgb(245, 247, 250);
            Font = new Font("Segoe UI", 10);

            var ustPanel = new Panel
            {
                Dock = DockStyle.Top,
                Height = 80,
                BackColor = Color.FromArgb(25, 39, 58)
            };
            Controls.Add(ustPanel);

            ustPanel.Controls.Add(new Label
            {
                Text = "Sistem Ayarları",
                Left = 24,
                Top = 16,
                Width = 300,
                Height = 28,
                ForeColor = Color.White,
                Font = new Font("Segoe UI", 14, FontStyle.Bold)
            });

            ustPanel.Controls.Add(new Label
            {
                Text = "Uygulama görünüm ve API sunucu bağlantı ayarları",
                Left = 26,
                Top = 46,
                Width = 400,
                Height = 20,
                ForeColor = Color.FromArgb(200, 208, 218),
                Font = new Font("Segoe UI", 9)
            });

            var themeGroup = new GroupBox
            {
                Text = "Arayüz Teması",
                Left = 24,
                Top = 95,
                Width = 416,
                Height = 85,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            Controls.Add(themeGroup);

            rdbLight = new RadioButton
            {
                Text = "Açık Tema (Light)",
                Left = 20,
                Top = 35,
                Width = 160,
                Checked = ThemeHelper.CurrentTheme == ThemeMode.Light,
                Font = new Font("Segoe UI", 10, FontStyle.Regular)
            };
            rdbDark = new RadioButton
            {
                Text = "Koyu Tema (Dark)",
                Left = 200,
                Top = 35,
                Width = 160,
                Checked = ThemeHelper.CurrentTheme == ThemeMode.Dark,
                Font = new Font("Segoe UI", 10, FontStyle.Regular)
            };
            themeGroup.Controls.Add(rdbLight);
            themeGroup.Controls.Add(rdbDark);

            btnKaydet = new Button
            {
                Text = "Kaydet ve Uygula",
                Left = 190,
                Top = 290,
                Width = 150,
                Height = 36,
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
                Text = "İptal",
                Left = 350,
                Top = 290,
                Width = 90,
                Height = 36,
                BackColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 10)
            };
            btnIptal.FlatAppearance.BorderColor = Color.FromArgb(209, 213, 219);
            btnIptal.Click += (sender, e) => Close();
            Controls.Add(btnIptal);

            Load += (sender, e) => ThemeHelper.ApplyTheme(this);
        }

        private string MevcutSunucuAdresiniAl()
        {
            try
            {
                string url = System.Configuration.ConfigurationManager.AppSettings["ApiUrl"];
                if (!string.IsNullOrEmpty(url))
                {
                    return url;
                }
            }
            catch { }
            return "http://localhost:8080";
        }

        private void BtnKaydet_Click(object sender, EventArgs e)
        {
            try
            {
                
                ThemeHelper.CurrentTheme = rdbDark.Checked ? ThemeMode.Dark : ThemeMode.Light;

                string yeniSunucu = txtSunucu.Text.Trim();
                if (string.IsNullOrEmpty(yeniSunucu))
                {
                    MessageBox.Show("Sunucu adresi boş olamaz.", "Hata", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    return;
                }

                SunucuBaglantisiniKaydet(yeniSunucu);

                MessageBox.Show("Ayarlar kaydedildi ve uygulandı.", "Başarılı", MessageBoxButtons.OK, MessageBoxIcon.Information);

                foreach (Form openForm in Application.OpenForms)
                {
                    ThemeHelper.ApplyTheme(openForm);
                }

                Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Ayarlar kaydedilirken hata oluştu:" + Environment.NewLine + ex.Message, "Hata", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void SunucuBaglantisiniKaydet(string sunucuUrl)
        {
            try
            {
                var config = System.Configuration.ConfigurationManager.OpenExeConfiguration(System.Configuration.ConfigurationUserLevel.None);
                if (config.AppSettings.Settings["ApiUrl"] != null)
                {
                    config.AppSettings.Settings["ApiUrl"].Value = sunucuUrl;
                }
                else
                {
                    config.AppSettings.Settings.Add("ApiUrl", sunucuUrl);
                }
                config.Save(System.Configuration.ConfigurationSaveMode.Modified);
                System.Configuration.ConfigurationManager.RefreshSection("appSettings");
            }
            catch (Exception ex)
            {
                throw new Exception("Bağlantı ayarları güncellenirken hata oluştu: " + ex.Message);
            }
        }
    }
}