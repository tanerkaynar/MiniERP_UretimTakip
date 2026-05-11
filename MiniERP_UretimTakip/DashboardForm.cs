using System;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class DashboardForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly Label lblUrunSayisi;
        private readonly Label lblToplamUretim;
        private readonly Label lblAktifMakine;
        private readonly Label lblKritikStok;
        private readonly Button btnYenile;

        public DashboardForm()
        {
            Text = "Mini ERP Dashboard";
            Width = 850;
            Height = 430;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label
            {
                Text = "Mini ERP Dashboard",
                Left = 30,
                Top = 25,
                Width = 400,
                Height = 40,
                Font = new Font("Segoe UI", 18, FontStyle.Bold)
            });

            lblUrunSayisi = Kart("Toplam Urun", 30, 100);
            lblToplamUretim = Kart("Toplam Uretim", 230, 100);
            lblAktifMakine = Kart("Aktif Makine", 430, 100);
            lblKritikStok = Kart("Kritik Stok", 630, 100);

            btnYenile = new Button { Text = "Yenile", Left = 30, Top = 260, Width = 120, Height = 40, Font = new Font("Segoe UI", 10, FontStyle.Bold) };
            btnYenile.Click += (sender, e) => DashboardYukle();
            Controls.Add(btnYenile);

            Load += (sender, e) => DashboardYukle();
        }

        private Label Kart(string baslik, int left, int top)
        {
            var label = new Label
            {
                Text = baslik + Environment.NewLine + "0",
                Left = left,
                Top = top,
                Width = 170,
                Height = 120,
                BorderStyle = BorderStyle.FixedSingle,
                TextAlign = ContentAlignment.MiddleCenter,
                Font = new Font("Segoe UI", 12, FontStyle.Bold)
            };
            Controls.Add(label);
            return label;
        }

        private void DashboardYukle()
        {
            lblUrunSayisi.Text = "Toplam Urun" + Environment.NewLine + TekDeger("SELECT COUNT(*) FROM dbo.Urunler WHERE AktifMi = 1");
            lblToplamUretim.Text = "Toplam Uretim" + Environment.NewLine + TekDeger("SELECT ISNULL(SUM(UretimAdedi), 0) FROM dbo.UretimKayitlari");
            lblAktifMakine.Text = "Aktif Makine" + Environment.NewLine + TekDeger("SELECT COUNT(*) FROM dbo.Makineler WHERE Durum = N'Aktif'");
            lblKritikStok.Text = "Kritik Stok" + Environment.NewLine + TekDeger("SELECT COUNT(*) FROM dbo.Urunler WHERE AktifMi = 1 AND StokMiktari <= 10");
        }

        private object TekDeger(string query)
        {
            using (var connection = new SqlConnection(ConnectionString))
            using (var command = new SqlCommand(query, connection))
            {
                connection.Open();
                return command.ExecuteScalar();
            }
        }
    }
}
