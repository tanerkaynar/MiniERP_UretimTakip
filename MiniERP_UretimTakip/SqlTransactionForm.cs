using System;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class SqlTransactionForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        private readonly ComboBox cmbUrunler;
        private readonly TextBox txtAdet;
        private readonly Button btnTransactionTest;

        public SqlTransactionForm()
        {
            Text = "C# SqlTransaction Testi";
            Width = 560;
            Height = 320;
            StartPosition = FormStartPosition.CenterScreen;

            Controls.Add(new Label
            {
                Text = "Transaction ile Uretim ve Stok Guncelleme",
                Left = 30,
                Top = 25,
                Width = 470,
                Height = 35,
                Font = new Font("Segoe UI", 14, FontStyle.Bold)
            });

            Controls.Add(Etiket("Urun", 30, 95));
            cmbUrunler = new ComboBox { Left = 140, Top = 91, Width = 280, DropDownStyle = ComboBoxStyle.DropDownList };
            Controls.Add(cmbUrunler);

            Controls.Add(Etiket("Uretim Adedi", 30, 140));
            txtAdet = new TextBox { Left = 140, Top = 136, Width = 120 };
            Controls.Add(txtAdet);

            btnTransactionTest = new Button
            {
                Text = "Transaction ile Kaydet",
                Left = 140,
                Top = 185,
                Width = 190,
                Height = 40,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };
            btnTransactionTest.Click += BtnTransactionTest_Click;
            Controls.Add(btnTransactionTest);

            Load += (sender, e) => UrunleriYukle();
        }

        private void UrunleriYukle()
        {
            using (var connection = new SqlConnection(ConnectionString))
            using (var command = new SqlCommand("SELECT UrunID, UrunAdi FROM dbo.Urunler WHERE AktifMi = 1 ORDER BY UrunAdi", connection))
            {
                connection.Open();
                var table = new System.Data.DataTable();
                table.Load(command.ExecuteReader());
                cmbUrunler.DataSource = table;
                cmbUrunler.DisplayMember = "UrunAdi";
                cmbUrunler.ValueMember = "UrunID";
            }
        }

        private void BtnTransactionTest_Click(object sender, EventArgs e)
        {
            if (cmbUrunler.SelectedValue == null)
            {
                MessageBox.Show("Urun secmelisiniz.");
                return;
            }

            if (!int.TryParse(txtAdet.Text, out int adet) || adet <= 0)
            {
                MessageBox.Show("Adet 0'dan buyuk sayi olmalidir.");
                return;
            }

            using (var connection = new SqlConnection(ConnectionString))
            {
                connection.Open();
                using (var transaction = connection.BeginTransaction())
                {
                    try
                    {
                        int urunID = Convert.ToInt32(cmbUrunler.SelectedValue);

                        using (var stokCommand = new SqlCommand(@"
                            UPDATE dbo.Urunler
                            SET StokMiktari = StokMiktari + @Adet
                            WHERE UrunID = @UrunID", connection, transaction))
                        {
                            stokCommand.Parameters.AddWithValue("@Adet", adet);
                            stokCommand.Parameters.AddWithValue("@UrunID", urunID);
                            stokCommand.ExecuteNonQuery();
                        }

                        using (var uretimCommand = new SqlCommand(@"
                            INSERT INTO dbo.UretimKayitlari
                                (UrunID, MakineID, PersonelID, UretimAdedi, UretimTarihi, Aciklama)
                            VALUES
                                (@UrunID,
                                 (SELECT TOP 1 MakineID FROM dbo.Makineler ORDER BY MakineID),
                                 (SELECT TOP 1 PersonelID FROM dbo.Personeller ORDER BY PersonelID),
                                 @Adet,
                                 GETDATE(),
                                 N'C# transaction ile eklendi')", connection, transaction))
                        {
                            uretimCommand.Parameters.AddWithValue("@UrunID", urunID);
                            uretimCommand.Parameters.AddWithValue("@Adet", adet);
                            uretimCommand.ExecuteNonQuery();
                        }

                        transaction.Commit();
                        MessageBox.Show("Transaction basarili. Uretim kaydi eklendi ve stok guncellendi.");
                    }
                    catch (Exception ex)
                    {
                        transaction.Rollback();
                        MessageBox.Show("Hata olustu, rollback yapildi: " + ex.Message);
                    }
                }
            }
        }

        private Label Etiket(string text, int left, int top)
        {
            return new Label { Text = text, Left = left, Top = top, Width = 105, Height = 28, Font = new Font("Segoe UI", 10) };
        }
    }
}
