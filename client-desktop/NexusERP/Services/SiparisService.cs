using System;
using System.Data;
using NexusERP.Helpers;

namespace NexusERP.Services
{
    public static class SiparisService
    {
        public static DataTable MusterileriGetir()
        {
            return ApiClient.GetDataTable("/api/musteriler");
        }

        public static DataTable SiparisleriGetir()
        {
            return ApiClient.GetDataTable("/api/siparisler");
        }

        public static DataTable SevkiyatBekleyenSiparisleriGetir()
        {
            return ApiClient.GetDataTable("/api/siparisler/bekleyen");
        }

        public static void SiparisKaydet(int musteriId, int urunId, int miktar, decimal birimFiyat)
        {
            var data = new
            {
                musteriid = musteriId,
                urunid = urunId,
                miktar = miktar,
                birimfiyat = birimFiyat
            };

            ApiClient.Post("/api/siparisler", data);
        }

        public static void SevkEt(int siparisId)
        {
            ApiClient.Post($"/api/siparisler/{siparisId}/sevk", null);
        }
    }
}