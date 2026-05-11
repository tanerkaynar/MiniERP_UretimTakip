using System;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

namespace MiniERP_UretimTakip
{
    public class BaglantiTestForm : Form
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        public BaglantiTestForm()
        {
            Text = "SQL Server Baglanti Testi";
            Width = 500;
            Height = 250;
            StartPosition = FormStartPosition.CenterScreen;

            var btnTest = new Button
            {
                Text = "Baglantiyi Test Et",
                Left = 140,
                Top = 70,
                Width = 200,
                Height = 55,
                Font = new Font("Segoe UI", 10, FontStyle.Bold)
            };

            btnTest.Click += BtnTest_Click;
            Controls.Add(btnTest);
        }

        private void BtnTest_Click(object sender, EventArgs e)
        {
            try
            {
                using (var connection = new SqlConnection(ConnectionString))
                {
                    connection.Open();
                    MessageBox.Show("SQL Server baglantisi basarili.");
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Baglanti sirasinda hata olustu: " + ex.Message);
            }
        }
    }
}
