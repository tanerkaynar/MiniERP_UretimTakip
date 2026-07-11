using System;

namespace NexusERP.Models
{
    public class IslemLog
    {
        public int LogID { get; set; }
        public string KullaniciAdi { get; set; }
        public string IslemTuru { get; set; }
        public string Aciklama { get; set; }
        public DateTime IslemTarihi { get; set; }
    }
}