using System;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class LoginForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly TextBox txtKullaniciAdi;
        private readonly TextBox txtParola;
        private readonly Button btnGiris;

        public LoginForm()
        {
            Text = "Kullanici Girisi";
            Width = 430;
            Height = 310;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label
            {
                Text = "Mini ERP Giris",
                Left = 40,
                Top = 25,
                Width = 300,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            });

            Controls.Add(Etiket("Kullanici Adi", 40, 90));
            txtKullaniciAdi = MetinKutusu(160, 86);
            Controls.Add(txtKullaniciAdi);

            Controls.Add(Etiket("Parola", 40, 135));
            txtParola = MetinKutusu(160, 131);
            txtParola.PasswordChar = '*';
            Controls.Add(txtParola);

            btnGiris = new Button
            {
                Text = "Giris Yap",
                Left = 160,
                Top = 185,
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

            using (var connection = new SqlConnection(ConnectionString))
            using (var command = new SqlCommand(@"
                SELECT COUNT(*)
                FROM dbo.Kullanicilar
                WHERE KullaniciAdi = @KullaniciAdi
                  AND Parola = @Parola
                  AND AktifMi = 1", connection))
            {
                command.Parameters.AddWithValue("@KullaniciAdi", txtKullaniciAdi.Text.Trim());
                command.Parameters.AddWithValue("@Parola", txtParola.Text);

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
            return new Label { Text = text, Left = left, Top = top, Width = 110, Height = 28, Font = new Font("Segoe UI", 10) };
        }

        private TextBox MetinKutusu(int left, int top)
        {
            return new TextBox { Left = left, Top = top, Width = 190, Height = 28, Font = new Font("Segoe UI", 10) };
        }
    }
}
