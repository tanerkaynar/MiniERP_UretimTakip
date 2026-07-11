using System;

namespace NexusERP.Models
{
    public class Urun
    {
        public int UrunID { get; set; }
        public string UrunAdi { get; set; }
        public int StokMiktari { get; set; }
        public decimal BirimFiyat { get; set; }
        public bool AktifMi { get; set; }
        public DateTime KayitTarihi { get; set; }
    }

}