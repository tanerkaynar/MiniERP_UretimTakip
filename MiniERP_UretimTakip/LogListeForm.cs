using System.Data;
using System.Data.SqlClient;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class LogListeForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly DataGridView dgvLoglar;

        public LogListeForm()
        {
            Text = "Islem Loglari";
            Width = 900;
            Height = 520;
            StartPosition = FormStartPosition.CenterScreen;

            dgvLoglar = new DataGridView
            {
                Left = 30,
                Top = 30,
                Width = 820,
                Height = 400,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill
            };
            Controls.Add(dgvLoglar);

            Load += (sender, e) => LoglariListele();
        }

        private void LoglariListele()
        {
            using (var connection = new SqlConnection(ConnectionString))
            {
                var adapter = new SqlDataAdapter(@"
                    SELECT TOP 100 LogID, KullaniciAdi, IslemTuru, Aciklama, IslemTarihi
                    FROM dbo.IslemLoglari
                    ORDER BY LogID DESC", connection);
                var table = new DataTable();
                adapter.Fill(table);
                dgvLoglar.DataSource = table;
            }
        }
    }
}
