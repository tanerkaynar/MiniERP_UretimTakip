using System;

namespace NexusERP.Helpers
{
    public static class LogHelper
    {
        public static void LogEkle(string kullaniciAdi, string islemTuru, string aciklama)
        {
            try
            {
                var data = new
                {
                    kullaniciadi = kullaniciAdi,
                    islemturu = islemTuru,
                    aciklama = aciklama
                };

                ApiClient.Post("/api/loglar", data);
            }
            catch
            {
                
            }
        }
    }
}