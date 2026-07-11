using System.Data;
using NexusERP.Helpers;

namespace NexusERP.Services
{
    public static class LogService
    {
        public static DataTable LoglariGetir()
        {
            return ApiClient.GetDataTable("/api/loglar");
        }

        public static void LogEkle(string kullaniciAdi, string islemTuru, string aciklama)
        {
            LogHelper.LogEkle(kullaniciAdi, islemTuru, aciklama);
        }
    }
}