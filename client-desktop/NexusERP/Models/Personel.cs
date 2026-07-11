using System;

namespace NexusERP.Models
{
    public class Personel
    {
        public int PersonelID { get; set; }
        public string AdSoyad { get; set; }
        public string Departman { get; set; }
        public bool AktifMi { get; set; }
        public DateTime KayitTarihi { get; set; }
    }

}