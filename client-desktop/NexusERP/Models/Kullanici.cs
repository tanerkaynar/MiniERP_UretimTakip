using System;

namespace NexusERP.Models
{
    public class Kullanici
    {
        public int KullaniciID { get; set; }
        public string KullaniciAdi { get; set; }
        public string Parola { get; set; }
        public string Rol { get; set; }
        public bool AktifMi { get; set; }
        public DateTime KayitTarihi { get; set; }
        public string ParolaHash { get; set; }
        public Personel Personel { get; set; }
    }
}