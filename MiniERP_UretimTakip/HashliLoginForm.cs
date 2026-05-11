using System;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;
using MiniERP_UretimTakip.Helpers;

namespace MiniERP_UretimTakip
{
    public class HashliLoginForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly TextBox txtKullaniciAdi;
        private readonly TextBox txtParola;
        private readonly Button btnGiris;

        public HashliLoginForm()
        {
            Text = "Hashli Kullanici Girisi";
            Width = 440;
            Height = 320;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label
            {
                Text = "Guvenli Giris",
                Left = 40,
                Top = 25,
                Width = 300,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            });

            Controls.Add(Etiket("Kullanici Adi", 40, 90));
            txtKullaniciAdi = MetinKutusu(165, 86);
            Controls.Add(txtKullaniciAdi);

            Controls.Add(Etiket("Parola", 40, 135));
            txtParola = MetinKutusu(165, 131);
            txtParola.PasswordChar = '*';
            Controls.Add(txtParola);

            btnGiris = new Button
            {
                Text = "Giris Yap",
                Left = 165,
                Top = 190,
                Width = 140,
                Height = 40,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnGiris.Click += BtnGiris_Click;
            Controls.Add(btnGiris);
        }

        private void BtnGiris_Click(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(txtKullaniciAdi.Text) || string.IsNullOrWhiteSpace(txtParola.Text))
            {
                MessageBox.Show("Kullanici adi ve parola bos olamaz.");
                return;
            }

            string girilenParolaHash = PasswordHelper.Sha256Hash(txtParola.Text);

            using (var connection = new SqlConnection(ConnectionString))
            using (var command = new SqlCommand(@"
                SELECT COUNT(*)
                FROM dbo.Kullanicilar
                WHERE KullaniciAdi = @KullaniciAdi
                  AND LOWER(ParolaHash) = @ParolaHash
                  AND AktifMi = 1", connection))
            {
                command.Parameters.AddWithValue("@KullaniciAdi", txtKullaniciAdi.Text.Trim());
                command.Parameters.AddWithValue("@ParolaHash", girilenParolaHash);

                connection.Open();
                int sonuc = Convert.ToInt32(command.ExecuteScalar());

                if (sonuc > 0)
                    MessageBox.Show("Giris basarili.");
                else
                    MessageBox.Show("Kullanici adi veya parola hatali.");
            }
        }

        private Label Etiket(string text, int left, int top)
        {
            return new Label { Text = text, Left = left, Top = top, Width = 115, Height = 28, Font = new Font("Segoe UI", 10) };
        }

        private TextBox MetinKutusu(int left, int top)
        {
            return new TextBox { Left = left, Top = top, Width = 190, Height = 28, Font = new Font("Segoe UI", 10) };
        }
    }
}
