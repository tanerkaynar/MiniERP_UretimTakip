using System;

namespace MiniERP_UretimTakip.Models
{

    public class Makine
    {
        public int MakineID { get; set; }
        public string MakineAdi { get; set; }
        public string MakineKodu { get; set; }
        public string Durum { get; set; }
        public DateTime KayitTarihi { get; set; }
    }

}
