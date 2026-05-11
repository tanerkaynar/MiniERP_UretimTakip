using System;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class UretimGirisForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly ComboBox cmbUrunler;
        private readonly ComboBox cmbMakineler;
        private readonly ComboBox cmbPersoneller;
        private readonly TextBox txtUretimAdedi;
        private readonly TextBox txtAciklama;
        private readonly Button btnKaydet;
        private readonly DataGridView dgvUretimKayitlari;

        public UretimGirisForm()
        {
            Text = "Uretim Giris Formu";
            Width = 1050;
            Height = 650;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label
            {
                Text = "Uretim Girisi",
                Left = 30,
                Top = 20,
                Width = 300,
                Height = 35,
                Font = new Font("Segoe UI", 16, FontStyle.Bold)
            });

            Controls.Add(Etiket("Urun", 30, 80));
            cmbUrunler = Combo(160, 76);
            Controls.Add(cmbUrunler);

            Controls.Add(Etiket("Makine", 30, 125));
            cmbMakineler = Combo(160, 121);
            Controls.Add(cmbMakineler);

            Controls.Add(Etiket("Personel", 30, 170));
            cmbPersoneller = Combo(160, 166);
            Controls.Add(cmbPersoneller);

            Controls.Add(Etiket("Uretim Adedi", 30, 215));
            txtUretimAdedi = MetinKutusu(160, 211);
            Controls.Add(txtUretimAdedi);

            Controls.Add(Etiket("Aciklama", 30, 260));
            txtAciklama = MetinKutusu(160, 256);
            Controls.Add(txtAciklama);

            btnKaydet = new Button
            {
                Text = "Uretim Kaydi Olustur",
                Left = 160,
                Top = 310,
                Width = 210,
                Height = 42,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnKaydet.Click += BtnKaydet_Click;
            Controls.Add(btnKaydet);

            dgvUretimKayitlari = new DataGridView
            {
                Left = 30,
                Top = 380,
                Width = 960,
                Height = 200,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AllowUserToAddRows = false
            };
            Controls.Add(dgvUretimKayitlari);

            Load += (sender, e) =>
            {
                UrunleriYukle();
                MakineleriYukle();
                PersonelleriYukle();
                UretimKayitlariniListele();
            };
        }

        private void BtnKaydet_Click(object sender, EventArgs e)
        {
            if (cmbUrunler.SelectedValue == null || cmbMakineler.SelectedValue == null || cmbPersoneller.SelectedValue == null)
            {
                MessageBox.Show("Urun, makine ve personel secimi zorunludur.");
                return;
            }

            if (!int.TryParse(txtUretimAdedi.Text, out int uretimAdedi) || uretimAdedi <= 0)
            {
                MessageBox.Show("Uretim adedi 0'dan buyuk bir sayi olmalidir.");
                return;
            }

            try
            {
                using (var connection = new SqlConnection(ConnectionString))
                {
                    connection.Open();

                    using (var transaction = connection.BeginTransaction())
                    {
                        try
                        {
                            using (var command = new SqlCommand(@"
                                INSERT INTO dbo.UretimKayitlari
                                    (UrunID, MakineID, PersonelID, UretimAdedi, UretimTarihi, Aciklama)
                                VALUES
                                    (@UrunID, @MakineID, @PersonelID, @UretimAdedi, GETDATE(), @Aciklama)", connection, transaction))
                            {
                                command.Parameters.AddWithValue("@UrunID", Convert.ToInt32(cmbUrunler.SelectedValue));
                                command.Parameters.AddWithValue("@MakineID", Convert.ToInt32(cmbMakineler.SelectedValue));
                                command.Parameters.AddWithValue("@PersonelID", Convert.ToInt32(cmbPersoneller.SelectedValue));
                                command.Parameters.AddWithValue("@UretimAdedi", uretimAdedi);
                                command.Parameters.AddWithValue("@Aciklama", txtAciklama.Text.Trim());
                                command.ExecuteNonQuery();
                            }

                            using (var stokCommand = new SqlCommand(@"
                                UPDATE dbo.Urunler
                                SET StokMiktari = StokMiktari + @UretimAdedi
                                WHERE UrunID = @UrunID", connection, transaction))
                            {
                                stokCommand.Parameters.AddWithValue("@UrunID", Convert.ToInt32(cmbUrunler.SelectedValue));
                                stokCommand.Parameters.AddWithValue("@UretimAdedi", uretimAdedi);
                                stokCommand.ExecuteNonQuery();
                            }

                            transaction.Commit();
                        }
                        catch
                        {
                            transaction.Rollback();
                            throw;
                        }
                    }
                }

                MessageBox.Show("Uretim kaydi olusturuldu ve stok miktari guncellendi.");
                txtUretimAdedi.Clear();
                txtAciklama.Clear();
                UretimKayitlariniListele();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Uretim kaydi sirasinda hata olustu: " + ex.Message);
            }
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

        private void UretimKayitlariniListele()
        {
            dgvUretimKayitlari.DataSource = TabloGetir(@"
                SELECT TOP 50
                    uk.UretimID,
                    u.UrunAdi,
                    m.MakineAdi,
                    p.AdSoyad AS PersonelAdi,
                    uk.UretimAdedi,
                    uk.UretimTarihi,
                    uk.Aciklama
                FROM dbo.UretimKayitlari uk
                INNER JOIN dbo.Urunler u ON uk.UrunID = u.UrunID
                INNER JOIN dbo.Makineler m ON uk.MakineID = m.MakineID
                INNER JOIN dbo.Personeller p ON uk.PersonelID = p.PersonelID
                ORDER BY uk.UretimTarihi DESC");
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

        private Label Etiket(string text, int left, int top)
        {
            return new Label { Text = text, Left = left, Top = top, Width = 120, Height = 28, Font = new Font("Segoe UI", 10) };
        }

        private ComboBox Combo(int left, int top)
        {
            return new ComboBox { Left = left, Top = top, Width = 280, Height = 30, Font = new Font("Segoe UI", 10), DropDownStyle = ComboBoxStyle.DropDownList };
        }

        private TextBox MetinKutusu(int left, int top)
        {
            return new TextBox { Left = left, Top = top, Width = 280, Height = 28, Font = new Font("Segoe UI", 10) };
        }
    }
}
