using System;
using System.Data;
using NexusERP.Helpers;

namespace NexusERP.Services
{
    public static class UretimService
    {
        public static DataTable TumUretimKayitlariniGetir()
        {
            return ApiClient.GetDataTable("/api/uretim");
        }

        public static DataTable TarihliUretimRaporuGetir(DateTime baslangic, DateTime bitis)
        {
            return ApiClient.GetDataTable($"/api/uretim/rapor?baslangic={baslangic:yyyy-MM-dd}&bitis={bitis:yyyy-MM-dd}");
        }

        public static DataTable GrupluRaporGetir(string tip)
        {
            return ApiClient.GetDataTable($"/api/uretim/gruplu?tip={Uri.EscapeDataString(tip)}");
        }

        public static DataTable DurusAnaliziGetir(DateTime baslangic, DateTime bitis)
        {
            return ApiClient.GetDataTable($"/api/uretim/durus?baslangic={baslangic:yyyy-MM-dd}&bitis={bitis:yyyy-MM-dd}");
        }

        public static DataTable AktifMakineleriGetir()
        {
            return ApiClient.GetDataTable("/api/makineler/aktif");
        }

        public static DataTable AktifPersonelleriGetir()
        {
            return ApiClient.GetDataTable("/api/personeller/aktif");
        }

        public static void UretimKaydet(int urunId, int makineId, int personelId, int uretimAdedi, string aciklama)
        {
            var data = new
            {
                urunid = urunId,
                makineid = makineId,
                personelid = personelId,
                uretimadedi = uretimAdedi,
                aciklama = aciklama
            };

            ApiClient.Post("/api/uretim", data);
        }

        public static void UretimSil(int uretimId)
        {
            ApiClient.Delete($"/api/uretim/{uretimId}");
        }
    }
}