using System;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class ComboBoxYuklemeForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly ComboBox cmbUrunler;
        private readonly ComboBox cmbMakineler;
        private readonly ComboBox cmbPersoneller;
        private readonly Button btnSecimleriGoster;

        public ComboBoxYuklemeForm()
        {
            Text = "ComboBox ile Iliskili Veri Secimi";
            Width = 620;
            Height = 360;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(Baslik());
            Controls.Add(Etiket("Urun", 30, 90));
            cmbUrunler = Combo(150, 86);
            Controls.Add(cmbUrunler);

            Controls.Add(Etiket("Makine", 30, 135));
            cmbMakineler = Combo(150, 131);
            Controls.Add(cmbMakineler);

            Controls.Add(Etiket("Personel", 30, 180));
            cmbPersoneller = Combo(150, 176);
            Controls.Add(cmbPersoneller);

            btnSecimleriGoster = new Button
            {
                Text = "Secimleri Goster",
                Left = 150,
                Top = 230,
                Width = 180,
                Height = 42,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnSecimleriGoster.Click += BtnSecimleriGoster_Click;
            Controls.Add(btnSecimleriGoster);

            Load += (sender, e) =>
            {
                UrunleriYukle();
                MakineleriYukle();
                PersonelleriYukle();
            };
        }

        private void UrunleriYukle()
        {
            cmbUrunler.DataSource = TabloGetir("SELECT UrunID, UrunAdi FROM dbo.Urunler WHERE AktifMi = 1 ORDER BY UrunAdi");
            cmbUrunler.DisplayMember = "UrunAdi";
            cmbUrunler.ValueMember = "UrunID";
        }

        private void MakineleriYukle()
        {
            cmbMakineler.DataSource = TabloGetir("SELECT MakineID, MakineAdi FROM dbo.Makineler WHERE Durum = N'Aktif' ORDER BY MakineAdi");
            cmbMakineler.DisplayMember = "MakineAdi";
            cmbMakineler.ValueMember = "MakineID";
        }

        private void PersonelleriYukle()
        {
            cmbPersoneller.DataSource = TabloGetir("SELECT PersonelID, AdSoyad FROM dbo.Personeller WHERE AktifMi = 1 ORDER BY AdSoyad");
            cmbPersoneller.DisplayMember = "AdSoyad";
            cmbPersoneller.ValueMember = "PersonelID";
        }

        private DataTable TabloGetir(string query)
        {
            using (var connection = new SqlConnection(ConnectionString))
            {
                var adapter = new SqlDataAdapter(query, connection);
                var table = new DataTable();
                adapter.Fill(table);
                return table;
            }
        }

        private void BtnSecimleriGoster_Click(object sender, EventArgs e)
        {
            MessageBox.Show(
                "Secilen UrunID: " + cmbUrunler.SelectedValue + Environment.NewLine +
                "Secilen MakineID: " + cmbMakineler.SelectedValue + Environment.NewLine +
                "Secilen PersonelID: " + cmbPersoneller.SelectedValue);
        }

        private Label Baslik()
        {
            return new Label
            {
                Text = "Uretim Secim Alanlari",
                Left = 30,
                Top = 25,
                Width = 400,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            };
        }

        private Label Etiket(string text, int left, int top)
        {
            return new Label { Text = text, Left = left, Top = top, Width = 100, Height = 28, Font = new Font("Segoe UI", 10) };
        }

        private ComboBox Combo(int left, int top)
        {
            return new ComboBox
            {
                Left = left,
                Top = top,
                Width = 300,
                Height = 30,
                Font = new Font("Segoe UI", 10),
                DropDownStyle = ComboBoxStyle.DropDownList
            };
        }
    }
}
