using System;

namespace NexusERP.Models
{
    public class Siparis
    {
        public int SiparisID { get; set; }
        public int MusteriID { get; set; }
        public DateTime SiparisTarihi { get; set; }
        public string Durum { get; set; }
    }
}