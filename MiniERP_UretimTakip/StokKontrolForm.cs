using System;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class StokKontrolForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly ComboBox cmbUrunler;
        private readonly TextBox txtMiktar;
        private readonly Label lblMevcutStok;
        private readonly Button btnKontrolEt;

        public StokKontrolForm()
        {
            Text = "Gelismis Stok Kontrolu";
            Width = 560;
            Height = 330;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label
            {
                Text = "Stok Yeterlilik Kontrolu",
                Left = 30,
                Top = 25,
                Width = 420,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            });

            Controls.Add(Etiket("Urun", 30, 95));
            cmbUrunler = new ComboBox { Left = 150, Top = 91, Width = 280, DropDownStyle = ComboBoxStyle.DropDownList };
            cmbUrunler.SelectedIndexChanged += (sender, e) => MevcutStokGoster();
            Controls.Add(cmbUrunler);

            lblMevcutStok = new Label { Text = "Mevcut Stok: -", Left = 150, Top = 128, Width = 250, Height = 28, Font = new Font("Segoe UI", 10, FontStyle.Bold) };
            Controls.Add(lblMevcutStok);

            Controls.Add(Etiket("Istenen Miktar", 30, 170));
            txtMiktar = new TextBox { Left = 150, Top = 166, Width = 130 };
            Controls.Add(txtMiktar);

            btnKontrolEt = new Button { Text = "Stok Kontrol Et", Left = 150, Top = 210, Width = 160, Height = 40, Font = new Font("Segoe UI", 10, FontStyle.Bold) };
            btnKontrolEt.Click += BtnKontrolEt_Click;
            Controls.Add(btnKontrolEt);

            Load += (sender, e) => UrunleriYukle();
        }

        private void UrunleriYukle()
        {
            using (var connection = new SqlConnection(ConnectionString))
            {
                var adapter = new SqlDataAdapter("SELECT UrunID, UrunAdi FROM dbo.Urunler WHERE AktifMi = 1 ORDER BY UrunAdi", connection);
                var table = new DataTable();
                adapter.Fill(table);
                cmbUrunler.DataSource = table;
                cmbUrunler.DisplayMember = "UrunAdi";
                cmbUrunler.ValueMember = "UrunID";
            }
        }

        private int MevcutStokGetir()
        {
            if (cmbUrunler.SelectedValue == null)
                return 0;

            using (var connection = new SqlConnection(ConnectionString))
            using (var command = new SqlCommand("SELECT StokMiktari FROM dbo.Urunler WHERE UrunID = @UrunID", connection))
            {
                command.Parameters.AddWithValue("@UrunID", Convert.ToInt32(cmbUrunler.SelectedValue));
                connection.Open();
                return Convert.ToInt32(command.ExecuteScalar());
            }
        }

        private void MevcutStokGoster()
        {
            if (cmbUrunler.SelectedValue == null || cmbUrunler.SelectedValue is DataRowView)
                return;

            lblMevcutStok.Text = "Mevcut Stok: " + MevcutStokGetir();
        }

        private void BtnKontrolEt_Click(object sender, EventArgs e)
        {
            if (!int.TryParse(txtMiktar.Text, out int istenenMiktar) || istenenMiktar <= 0)
            {
                MessageBox.Show("Istenen miktar 0'dan buyuk sayi olmalidir.");
                return;
            }

            int mevcutStok = MevcutStokGetir();
            if (mevcutStok < istenenMiktar)
                MessageBox.Show("Yetersiz stok. Mevcut: " + mevcutStok + ", istenen: " + istenenMiktar);
            else
                MessageBox.Show("Stok yeterli. Sevkiyat veya siparis islemi yapilabilir.");
        }

        private Label Etiket(string text, int left, int top)
        {
            return new Label { Text = text, Left = left, Top = top, Width = 115, Height = 28, Font = new Font("Segoe UI", 10) };
        }
    }
}
