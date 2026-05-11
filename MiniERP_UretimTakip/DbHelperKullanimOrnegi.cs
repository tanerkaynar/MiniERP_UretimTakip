using System.Data;
using System.Windows.Forms;
using MiniERP_UretimTakip.Helpers;

namespace MiniERP_UretimTakip
{
    public class DbHelperKullanimOrnegi : Form
    {
        private readonly DataGridView dgvUrunler;
        private readonly Button btnListele;

        public DbHelperKullanimOrnegi()
        {
            Text = "DbHelper Kullanim Ornegi";
            Width = 800;
            Height = 480;
            StartPosition = FormStartPosition.CenterScreen;

            btnListele = new Button { Text = "Urunleri Listele", Left = 30, Top = 25, Width = 150, Height = 36 };
            btnListele.Click += (sender, e) => Listele();
            Controls.Add(btnListele);

            dgvUrunler = new DataGridView
            {
                Left = 30,
                Top = 80,
                Width = 720,
                Height = 320,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill
            };
            Controls.Add(dgvUrunler);

            Load += (sender, e) => Listele();
        }

        private void Listele()
        {
            DataTable table = DbHelper.GetDataTable(@"
                SELECT UrunID, UrunAdi, StokMiktari, BirimFiyat
                FROM dbo.Urunler
                ORDER BY UrunID DESC");

            dgvUrunler.DataSource = table;
        }
    }
}
