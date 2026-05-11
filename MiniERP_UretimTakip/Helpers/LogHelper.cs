using System;
using System.Data.SqlClient;

namespace MiniERP_UretimTakip.Helpers
{
    public static class LogHelper
    {
        private const string ConnectionString =
            @"Server=TANER\SQLEXPRESS;Database=MiniERP_UretimTakip;Trusted_Connection=True;TrustServerCertificate=True;";

        public static void LogEkle(string kullaniciAdi, string islemTuru, string aciklama)
        {
            try
            {
                using (var connection = new SqlConnection(ConnectionString))
                using (var command = new SqlCommand(@"
                    INSERT INTO dbo.IslemLoglari (KullaniciAdi, IslemTuru, Aciklama)
                    VALUES (@KullaniciAdi, @IslemTuru, @Aciklama)", connection))
                {
                    command.Parameters.AddWithValue("@KullaniciAdi", kullaniciAdi);
                    command.Parameters.AddWithValue("@IslemTuru", islemTuru);
                    command.Parameters.AddWithValue("@Aciklama", aciklama);
                    connection.Open();
                    command.ExecuteNonQuery();
                }
            }
            catch
            {
                // Log hatasi uygulamanin ana islemini durdurmasin.
            }
        }
    }
}
