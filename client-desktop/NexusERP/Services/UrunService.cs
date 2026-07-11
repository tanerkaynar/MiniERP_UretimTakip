using System;
using System.Data;
using NexusERP.Helpers;

namespace NexusERP.Services
{
    public static class UrunService
    {
        public static DataTable TumUrunleriGetir()
        {
            return ApiClient.GetDataTable("/api/urunler");
        }

        public static DataTable AktifUrunleriGetir()
        {
            return ApiClient.GetDataTable("/api/urunler/aktif");
        }

        public static int UrunEkle(string urunAdi, int stokMiktari, decimal birimFiyat)
        {
            var data = new
            {
                urunadi = urunAdi,
                stokmiktari = stokMiktari,
                birimfiyat = birimFiyat
            };

            string res = ApiClient.Post("/api/urunler", data);
            return !string.IsNullOrWhiteSpace(res) ? 1 : 0;
        }

        public static int UrunGuncelle(int urunId, string urunAdi, int stokMiktari, decimal birimFiyat)
        {
            var data = new
            {
                urunadi = urunAdi,
                stokmiktari = stokMiktari,
                birimfiyat = birimFiyat
            };

            string res = ApiClient.Put($"/api/urunler/{urunId}", data);
            return !string.IsNullOrWhiteSpace(res) ? 1 : 0;
        }

        public static int UrunSil(int urunId)
        {
            string res = ApiClient.Delete($"/api/urunler/{urunId}");
            return 1;
        }

        public static int MevcutStokGetir(int urunId)
        {
            string res = ApiClient.Get($"/api/urunler/{urunId}/stok");
            return int.TryParse(res, out int stok) ? stok : 0;
        }
    }
}