using System;
using NexusERP.Helpers;
using NexusERP.Models;
using Newtonsoft.Json;

namespace NexusERP.Services
{
    public static class KullaniciService
    {
        public static Kullanici Dogrula(string kullaniciAdi, string parola)
        {
            try
            {
                var requestData = new
                {
                    kullaniciadi = kullaniciAdi,
                    parola = parola
                };

                string json = ApiClient.Post("/api/kullanicilar/login", requestData);
                return JsonConvert.DeserializeObject<Kullanici>(json);
            }
            catch
            {
                return null;
            }
        }

        public static bool KullaniciKaydet(string kullaniciAdi, string parola, string rol, int? personelId)
        {
            var requestData = new
            {
                kullaniciadi = kullaniciAdi,
                parola = parola,
                rol = rol,
                personelid = personelId
            };

            try
            {
                string json = ApiClient.Post("/api/kullanicilar/register", requestData);
                return !string.IsNullOrWhiteSpace(json);
            }
            catch (Exception ex)
            {
                throw new Exception(ex.Message);
            }
        }
    }
}